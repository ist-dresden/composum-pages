<%@page session="false" pageEncoding="UTF-8" %><%--
Hook for extensions (buttons etc.) within the label.
--%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2"
%><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"
%><%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0"
%><cpn:defineObjects/><%--
We include the labelextensions directly since some of the ChatGPT extensions have complicated visibility conditions.
If needed, we could later also render icons directly here with the normal mechanism via tool.iconClass etc., when
tool.resourceType is null.
--%><cpn:component var="widgetTools" type="com.composum.pages.stage.tools.WidgetTools"><%--
    --%><c:forEach var="tool" items="${widgetTools.labelExtensionComponentList}">
        <sling:include resourceType="${tool.resourceType}"  replaceSelectors="labelextension" />
    </c:forEach><%--
--%></cpn:component>
