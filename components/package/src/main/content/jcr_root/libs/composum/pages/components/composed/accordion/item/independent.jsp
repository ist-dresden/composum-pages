<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineObjects/><%-- the 'independent' selector is used to enable multiple open items --%>
<cpp:container var="model" type="com.composum.pages.components.model.composed.accordion.AccordionItem"
               cssAdd="panel panel-default">
    <div id="${modelId}_head" class="${modelCSS}_head panel-heading" role="tab">
        <cpn:div tagName="${model.titleTagName}" class="panel-title">
            <a role="button" data-toggle="collapse" href="#${modelId}_body"
               aria-expanded="${model.initialOpen||model.editMode}" aria-controls="${modelId}_body"
               class="${modelCSS}_toggle${model.initialOpen||model.editMode?'':' collapsed'}">
                <cpn:text value="${model.title}"/>
            </a>
        </cpn:div>
    </div>
    <div id="${modelId}_body"
         class="${modelCSS}_body panel-collapse collapse ${model.initialOpen||model.editMode?'in':''}"
         role="tabpanel" aria-labelledby="${modelId}_head">
        <div class="${modelCSS}_content panel-body">
            <c:forEach items="${model.elements}" var="element" varStatus="loop">
                <cpp:include resource="${element.resource}"/>
            </c:forEach>
        </div>
    </div>
</cpp:container>
