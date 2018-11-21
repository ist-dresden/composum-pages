/*
 * copyright (c) 2015ff IST GmbH Dresden, Germany - https://www.ist-software.com
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.pages.commons.model;

import com.composum.pages.commons.util.IteratorCascade;
import com.composum.sling.core.BeanContext;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class IParSys extends ParSys {

    public static final String PN_PARSYS_POLICY = "parsysPolicy";
    public static final String INHERITED_FIRST_POLICY = "inheritedFirst";

    public static final String PN_PARENT_CANCELLED = "parentInheritanceCancelled";
    public static final String PN_CHILD_CANCELLED = "childInheritanceCancelled";

    public static final String PN_PARSYS_ORDER = "parsysOrder";
    public static final int DEF_PARSYS_ORDER = 50;

    protected static class IParsSysComparator implements Comparator<Element> {

        @Override
        public int compare(Element elem1, Element elem2) {
            int orderHint1 = getElementOrderHint(elem1);
            int orderHint2 = getElementOrderHint(elem2);
            return Integer.compare(orderHint1, orderHint2);
        }

        protected int getElementOrderHint(Element element) {
            return element.getProperty(PN_PARSYS_ORDER, DEF_PARSYS_ORDER);
        }
    }

    public static final IParsSysComparator IPARSYS_COMPARATOR = new IParsSysComparator();

    private transient Boolean parentInheritanceCancelled;
    private transient Boolean childInheritanceCancelled;
    private transient Boolean inheritedFirst;

    public boolean isParentInheritanceCancelled(){
        if (parentInheritanceCancelled == null) {
            parentInheritanceCancelled = getProperty(PN_PARENT_CANCELLED, Boolean.FALSE);
        }
        return parentInheritanceCancelled;
    }

    public boolean isChildInheritanceCancelled(){
        if (childInheritanceCancelled == null) {
            childInheritanceCancelled = getProperty(PN_CHILD_CANCELLED, Boolean.FALSE);
        }
        return childInheritanceCancelled;
    }

    public boolean isInheritedFirst(){
        if (inheritedFirst == null) {
            inheritedFirst = getProperty(PN_PARSYS_POLICY, "").equalsIgnoreCase(INHERITED_FIRST_POLICY);
        }
        return inheritedFirst;
    }

    @Override
    protected Iterator<Resource> retrieveElementResources() {
        IParSys parent = null;
        if (!isParentInheritanceCancelled()) {
            Page page = getContainingPage();
            if (page != null) {
                String contentPath = page.getContent().getPath() + "/";
                String containerPath = getPath();
                String relativePath = containerPath.substring(contentPath.length());
                BeanContext context = getContext();
                ResourceResolver resolver = context.getResolver();
                Resource p = null;
                while (p == null && (page = page.getParentPage()) != null) {
                    p = page.getContent().getResource().getChild(relativePath);
                    if (p != null && !Container.isContainer(resolver, p, null)) {
                        p = null;
                    }
                }
                if (p != null) {
                    parent = new IParSys();
                    parent.initialize(context, p);
                    if (parent.isChildInheritanceCancelled()) {
                        parent = null;
                    }
                }
            }
        }
        Iterator<Resource> children = super.retrieveElementResources();
        return parent == null ? children
                : (isInheritedFirst()
                ? new IteratorCascade<>(parent.retrieveElementResources(), children)
                : new IteratorCascade<>(children, parent.retrieveElementResources()));

    }

    /**
     * arrange elements according to the 'parsysOrder' element property
     */
    @Override
    protected void arrangeElements(List<Element> elementList) {
        Collections.sort(elementList, IPARSYS_COMPARATOR);
    }
}
