<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<cpp:model var="model" type="com.composum.pages.components.model.time.TimeNavigator" scope="request"
           cssBase="composum-pages-components-time-navigator">
    <div class="${modelCSS}_header">
        <cpn:div test="${model.showNavigation}"
                 class="${modelCSS}_move fa fa-chevron-left"
                 data-range="${model.backwardRange}"></cpn:div>
        <cpn:text class="${modelCSS}_label" value="${model.label}"/>
        <cpn:div test="${model.showNavigation}"
                 class="${modelCSS}_move fa fa-chevron-right"
                 data-range="${model.forwardRange}"></cpn:div>
    </div>
    <div class="${modelCSS}_items">
        <sling:call script="items.jsp"/>
    </div>
</cpp:model>
