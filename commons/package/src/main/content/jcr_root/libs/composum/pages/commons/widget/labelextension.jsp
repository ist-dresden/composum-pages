<%@page session="false" pageEncoding="UTF-8" %>
<%-- Hook for extensions (buttons etc.) within the label. --%>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--@elvariable id="widget" type="com.composum.pages.commons.taglib.AbstractWidgetTag"--%>
<c:forEach items="${widget.model.pagesPluginService.widgetLabelExtensions}" var="extension">
    <sling:include resourceType="${extension}"/>
</c:forEach>
