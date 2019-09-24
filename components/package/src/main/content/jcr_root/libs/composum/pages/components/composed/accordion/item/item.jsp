<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineObjects/>
<cpp:container var="model" type="com.composum.pages.components.model.composed.accordion.AccordionItem"
               cssAdd="panel panel-default">
    <div id="${modelId}_head" class="panel-heading" role="tab">
        <cpn:div tagName="${model.titleTagName}" class="panel-title">
            <%-- the item is using the id of the accordion - available in request scope --%>
            <a role="button" data-toggle="collapse" data-parent="#${accordionId}" href="#${modelId}_body"
               aria-expanded="${model.initialOpen||model.editMode}" aria-controls="${modelId}_body">
                <cpn:text value="${model.title}"/>
            </a>
        </cpn:div>
    </div>
    <div id="${modelId}_body" class="panel-collapse collapse ${model.initialOpen||model.editMode?'in':''}"
         role="tabpanel" aria-labelledby="${modelId}_head">
        <div class="panel-body">
            <c:forEach items="${model.elements}" var="element" varStatus="loop">
                <cpp:include resource="${element.resource}"/>
            </c:forEach>
        </div>
    </div>
</cpp:container>
