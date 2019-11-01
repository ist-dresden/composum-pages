package com.composum.pages.stage.model.tools;

import com.composum.pages.commons.model.Component;
import com.composum.pages.commons.model.Container;
import com.composum.pages.commons.model.Element;
import com.composum.pages.commons.model.GenericModel;
import com.composum.pages.commons.model.Model;
import com.composum.pages.commons.model.Page;
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
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * the model for property comparation - implements the comparator itself
 */
public class PropertiesComparatorModel extends AbstractServletBean {

    protected class ComparatorRef {

        public BeanContext context;
        public Resource resource;
        public Locale locale;

        public ComparatorRef(@Nonnull final BeanContext base, @Nonnull final String path,
                             @Nullable final String versionUuid, @Nullable final Locale locale) {
            this.context = base;
            this.locale = locale;
            if (StringUtils.isNotBlank(versionUuid)) {

            }
            if (this.resource == null) {
                this.resource = this.context.getResolver().getResource(path);
            }
        }
    }

    protected PropertiesComparatorNode root;
    protected String property;
    protected ResourceFilter propertyFilter;

    protected ComparatorRef left;
    protected ComparatorRef right;

    public PropertiesComparatorModel(@Nonnull final BeanContext context,
                                     @Nonnull final String left, @Nullable final String leftVersionUuid, @Nullable final Locale leftLocale,
                                     @Nonnull final String right, @Nullable final String rightVersionUuid, @Nullable final Locale rightLocale,
                                     @Nullable final String property, @Nullable final ResourceFilter propertyFilter) {
        initialize(context);

        this.property = property;
        this.propertyFilter = propertyFilter;

        this.left = new ComparatorRef(context, left, leftVersionUuid, leftLocale);
        this.right = new ComparatorRef(context, right, rightVersionUuid, rightLocale);

        this.root = scan(this.left.resource, this.right.resource);
    }

    @Nonnull
    public PropertiesComparatorNode getRoot() {
        return root;
    }

    @Nonnull
    protected PropertiesComparatorNode scan(@Nullable final Resource left, @Nullable final Resource right) {
        GenericModel leftModel = left != null ? new GenericModel(this.left.context, left) : null;
        GenericModel rightModel = right != null ? new GenericModel(this.right.context, right) : null;
        Component leftType = leftModel != null ? leftModel.getComponent() : null;
        Component rightType = rightModel != null ? rightModel.getComponent() : null;
        PropertiesComparatorNode node = new PropertiesComparatorNode(leftModel, rightModel);
        if (StringUtils.isNotBlank(property) && !"*".equals(property)) {
            node.setProperty(property,
                    getValue(leftModel, property, this.left.locale),
                    getValue(rightModel, property, this.right.locale));
        } else {
            Component.Properties leftProps = leftType != null ? leftType.getComponentProperties(propertyFilter) : null;
            Component.Properties rightProps = rightType != null ? rightType.getComponentProperties(propertyFilter) : null;
            if (leftProps != null) {
                for (Component.Property compProp : leftProps.values()) {
                    String propName = compProp.getName();
                    node.setProperty(propName,
                            getValue(leftModel, propName, this.left.locale),
                            getValue(rightModel, propName, this.right.locale));
                }
            }
            if (rightProps != null) {
                for (Component.Property compProp : rightProps.values()) {
                    String propName = compProp.getName();
                    if (node.getProperty(propName) == null) {
                        node.setProperty(propName,
                                getValue(leftModel, propName, this.left.locale),
                                getValue(rightModel, propName, this.right.locale));
                    }
                }
            }
        }
        if ("*".equals(property)) {
            Map<String, Model> leftElements = getElements(leftModel);
            Map<String, Model> rightElements = getElements(rightModel);
            Iterator<Map.Entry<String, Model>> leftIt = leftElements.entrySet().iterator();
            Iterator<Map.Entry<String, Model>> rightIt = rightElements.entrySet().iterator();
            while (leftIt.hasNext() || rightIt.hasNext()) {
                Resource leftElement = leftIt.hasNext() ? leftIt.next().getValue().getResource() : null;
                Resource rightElement = rightIt.hasNext() ? rightIt.next().getValue().getResource() : null;
                node.addChild(scan(leftElement, rightElement));
            }
        }
        return node;
    }

    @Nonnull
    protected Map<String, Model> getElements(@Nullable final GenericModel model) {
        Map<String, Model> elements = null;
        if (model != null) {
            Model delegate = model.getDelegate();
            if (delegate instanceof Page) {
                elements = ((Page) delegate).getContent().getElements();
            } else if (delegate instanceof Container) {
                elements = new LinkedHashMap<>();
                List<Element> sequence = ((Container) delegate).getElements();
                for (Element element : sequence) {
                    elements.put(element.getName(), element);
                }
            }
        }
        return elements != null ? elements : Collections.emptyMap();
    }

    @Nullable
    protected Object getValue(@Nullable final GenericModel model, @Nonnull final String name,
                              @Nullable final Locale locale) {
        return model != null
                ? model.getValueMap().get(InternationalizationStrategy.I18NFOLDER.getI18nPath(locale, name))
                : null;
    }

    public void toJson(@Nonnull final JsonWriter writer) throws IOException {
        root.toJson(writer);
    }
}
