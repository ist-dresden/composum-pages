<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineObjects/><%--
 the accordion is used by the items, so it must be defined in the request scope;
 but if an accordion contains an accordion the accordion object must be replaced!
 --> scope="request", replace="true" and var="accordion"
--%>
<cpp:container var="accordion" type="com.composum.pages.components.model.composed.accordion.Accordion"
               scope="request" replace="true" tagId="@{accordionId}" cssAdd="@{accordionCSS}_tabbed">
    <ul class="@{accordionCSS}_tab-nav nav nav-tabs" role="tablist">
        <c:forEach items="${accordion.elements}" var="element" varStatus="loop">
            <cpp:include resource="${element.resource}" replaceSelectors="tabNav"/>
        </c:forEach>
    </ul>
    <div class="@{accordionCSS}_tab-content tab-content">
        <c:forEach items="${accordion.elements}" var="element" varStatus="loop">
            <cpp:include resource="${element.resource}" replaceSelectors="tabContent"/>
        </c:forEach>
    </div>
</cpp:container>
