<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineObjects/><%--
 the accordion is used by the items, so it must be defined in the request scope;
 but if an accordion contains an accordion the accordion object must be replaced!
 --> scope="request" replace="true"
--%>
<cpp:container var="accordion" type="com.composum.pages.components.model.composed.accordion.Accordion"
               tagId="@{accordionId}" scope="request" replace="true" role="tablist"
               cssBase="composum-pages-components-accordion" cssAdd="panel-group">
    <c:forEach items="${accordion.elements}" var="element" varStatus="loop">
        <cpp:include resource="${element.resource}" replaceSelectors="${accordion.behavior}"/>
    </c:forEach>
</cpp:container>
