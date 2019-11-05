package com.composum.pages.stage.model.tools;

import com.composum.pages.commons.model.Component;
import com.composum.pages.commons.model.Container;
import com.composum.pages.commons.model.Element;
import com.composum.pages.commons.model.GenericModel;
import com.composum.pages.commons.model.Model;
import com.composum.pages.commons.model.Page;
import com.composum.pages.commons.model.PageContent;
import com.composum.platform.models.annotations.InternationalizationStrategy;
import com.composum.sling.core.AbstractServletBean;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.filter.ResourceFilter;
import com.google.gson.stream.JsonWriter;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * the model for property comparation - implements the comparator itself
 */
public class PropertiesComparatorModel extends AbstractServletBean {

    protected PropertiesComparatorRoot root;
    protected String property;
    protected ResourceFilter propertyFilter;
    protected boolean skipEqualProperties;

    public PropertiesComparatorModel(@Nonnull final BeanContext context,
                                     @Nonnull final String left, @Nullable final String leftVersionUuid, @Nullable final Locale leftLocale,
                                     @Nonnull final String right, @Nullable final String rightVersionUuid, @Nullable final Locale rightLocale,
                                     @Nullable final String property, @Nullable final ResourceFilter propertyFilter,
                                     boolean skipEqualProperties, boolean highlightDifferences) {
        initialize(context);
        this.property = property;
        this.propertyFilter = propertyFilter;
        this.skipEqualProperties = skipEqualProperties;
        this.root = new PropertiesComparatorRoot(context,
                left, leftLocale, leftVersionUuid,
                right, rightLocale, rightVersionUuid,
                highlightDifferences);
        scan(this.root);
    }

    @Nonnull
    public PropertiesComparatorNode getRoot() {
        return root;
    }

    public void toJson(@Nonnull final JsonWriter writer) throws IOException {
        root.toJson(writer);
    }

    @Nonnull
    protected PropertiesComparatorNode nextNode(@Nullable final Resource leftResource,
                                                @Nullable final Resource rightResource) {
        GenericModel leftModel = leftResource != null
                ? new GenericModel(Objects.requireNonNull(this.root.getLeft()).getContext(), leftResource)
                : null;
        GenericModel rightModel = rightResource != null
                ? new GenericModel(Objects.requireNonNull(this.root.getRight()).getContext(), rightResource)
                : null;
        return new PropertiesComparatorNode(leftModel, rightModel);
    }

    @Nullable
    protected PropertiesComparatorNode scan(@Nonnull final PropertiesComparatorNode node) {
        Model left = getComparableModel(node.getLeft());
        Model right = getComparableModel(node.getRight());
        Locale leftLocale = getLocale(this.root.getLeft());
        Locale rightLocale = getLocale(this.root.getRight());
        if (StringUtils.isNotBlank(property) && !"*".equals(property)) {
            node.setProperty(property,
                    getValue(left, property, leftLocale),
                    getValue(right, property, rightLocale), skipEqualProperties);
        } else {
            Component.Properties leftProps = getProperties(left);
            Component.Properties rightProps = getProperties(right);
            if (leftProps != null) {
                for (Component.Property compProp : leftProps.values()) {
                    String propName = compProp.getName();
                    node.setProperty(propName,
                            getValue(left, propName, leftLocale),
                            getValue(right, propName, rightLocale), skipEqualProperties);
                }
            }
            if (rightProps != null) {
                for (Component.Property compProp : rightProps.values()) {
                    String propName = compProp.getName();
                    if (node.getProperty(propName) == null) {
                        node.setProperty(propName,
                                getValue(left, propName, leftLocale),
                                getValue(right, propName, rightLocale), skipEqualProperties);
                    }
                }
            }
        }
        if ("*".equals(property)) {
            Map<String, Model> leftElements = getElements(left);
            Map<String, Model> rightElements = getElements(right);
            List<String> leftNames = new ArrayList<>(leftElements.keySet());
            List<String> rightNames = new ArrayList<>(rightElements.keySet());
            for (int li = 0, ri = 0; li < leftNames.size() || ri < rightNames.size(); li++, ri++) {
                if (!leftNames.get(li).equals(rightNames.get(ri))) {
                    int ii = ri;
                    while (++ii < rightNames.size() && !leftNames.get(li).equals(rightNames.get(ii))) ;
                    if (ii == rightNames.size()) {
                        rightNames.add(ri, null);
                    } else {
                        ii = li;
                        while (++ii < leftNames.size() && !rightNames.get(ri).equals(rightNames.get(ii))) ;
                        if (ii == leftNames.size()) {
                            leftNames.add(li, null);
                        }
                    }
                }
            }
            Iterator<String> leftIt = leftNames.iterator();
            Iterator<String> rightIt = rightNames.iterator();
            while (leftIt.hasNext() || rightIt.hasNext()) {
                String leftName = leftIt.hasNext() ? leftIt.next() : null;
                String rightName = rightIt.hasNext() ? rightIt.next() : null;
                Resource leftElement = leftName !=null ? leftElements.get(leftName).getResource() : null;
                Resource rightElement = rightName !=null ? rightElements.get(rightName).getResource() : null;
                PropertiesComparatorNode child = scan(nextNode(leftElement, rightElement));
                if (child != null) {
                    node.addChild(child);
                }
            }
        }
        return node.getProperties().size() > 0 || node.getNodes().size() > 0 ? node : null;
    }

    @Nullable
    protected Model getComparableModel(@Nullable final GenericModel model) {
        if (model != null) {
            Model delegate = model.getDelegate();
            if (delegate instanceof Page) {
                return ((Page) delegate).getContent();
            }
            return delegate;
        }
        return null;
    }

    protected Locale getLocale(@Nullable final PropertiesComparatorRoot.ComparatorRef root) {
        Locale locale = null;
        if (root != null) {
            locale = root.getLocale();
            if (locale != null && locale.equals(root.getLanguages().getDefaultLanguage().getLocale())) {
                locale = null;
            }
        }
        return locale;
    }

    @Nullable
    protected Component.Properties getProperties(@Nullable final Model model) {
        Component type = model != null ? model.getComponent() : null;
        return type != null ? type.getComponentProperties(propertyFilter) : null;
    }

    @Nonnull
    protected Map<String, Model> getElements(@Nullable final Model model) {
        Map<String, Model> elements = null;
        if (model instanceof PageContent) {
            elements = ((PageContent) model).getElements();
        } else if (model instanceof Container) {
            elements = new LinkedHashMap<>();
            List<Element> sequence = ((Container) model).getElements();
            for (Element element : sequence) {
                elements.put(element.getName(), element);
            }
        }
        return elements != null ? elements : Collections.emptyMap();
    }

    @Nullable
    protected Object getValue(@Nullable final Model model, @Nonnull final String name,
                              @Nullable final Locale locale) {
        return model != null
                ? model.getValueMap().get(InternationalizationStrategy.I18NFOLDER.getI18nPath(locale, name))
                : null;
    }
}
