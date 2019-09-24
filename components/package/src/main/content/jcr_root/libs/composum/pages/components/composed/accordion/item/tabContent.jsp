<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineObjects/>
<cpp:container var="model" type="com.composum.pages.components.model.composed.accordion.AccordionItem"
               tagId="@{modelId}_content" role="tabpanel" cssAdd="tab-pane @{model.initialOpen?'active':''}">
    <c:forEach items="${model.elements}" var="element" varStatus="loop">
        <cpp:include resource="${element.resource}"/>
    </c:forEach>
</cpp:container>
