<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/>
<cpn:component var="current" type="com.composum.pages.stage.model.tools.PropertiesComparatorNode" scope="request">
    <ul class="composum-pages-tools-comparator_properties">
        <c:forEach items="${current.properties}" var="property" varStatus="loop">
            <li class="composum-pages-tools-comparator_property ${property.equal?'equal-property':'different-property'}">
                <div class="composum-pages-tools-comparator_property-name">
                        ${cpn:text(property.name)}
                </div>
                <div class="composum-pages-tools-comparator_property-left composum-pages-tools-comparator_property-value">
                    <c:if test="${!property.richText}">${cpn:text(property.left)}</c:if>
                    <c:if test="${property.richText}">${cpn:rich(slingRequest,property.left)}</c:if>
                </div>
                <div class="composum-pages-tools-comparator_property-right composum-pages-tools-comparator_property-value">
                    <c:if test="${!property.richText}">${cpn:text(property.right)}</c:if>
                    <c:if test="${property.richText}">${cpn:rich(slingRequest,property.right)}</c:if>
                </div>
            </li>
        </c:forEach>
    </ul>
    <ul class="composum-pages-tools-comparator_nodes">
        <c:forEach items="${current.nodes}" var="node" varStatus="loop">
            <li class="">
                <% slingRequest.setAttribute("current", pageContext.findAttribute("node")); %>
                <sling:include replaceSelectors="drilldown"/>
                <% slingRequest.setAttribute("current", current); %>
            </li>
        </c:forEach>
    </ul>
</cpn:component>
