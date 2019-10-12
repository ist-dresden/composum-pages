<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineObjects/>
<cpp:element id="@{modelId}" var="model" type="com.composum.pages.components.model.element.IFrame"
             test="@{model.valid||model.editMode}" cssAdd="@{modelCSS}_panel @{model.expandable?'expandable':''}">
    <div class="${modelCSS}_panel panel panel-default">
        <c:if test="${model.showHeading}">
            <div class="${modelCSS}_heading panel-heading">
                <cpn:text tagName="span" class="${modelCSS}_title" value="${model.title}"/>
                <c:if test="${model.expandable}">
                    <cpn:text tagName="button"
                              class="${modelCSS}_button btn btn-xs btn-default ${modelCSS}_expand"
                              value="expand view"/>
                    <cpn:text tagName="button"
                              class="${modelCSS}_button btn btn-xs btn-default ${modelCSS}_collapse"
                              value="collapse view"/>
                </c:if>
            </div>
        </c:if>
        <div class="${modelCSS}_content panel-body">
            <cpp:include replaceSelectors="${model.editMode?'placeholder':'view'}"/>
        </div>
        <cpn:text class="${modelCSS}_footer panel-footer" value="${model.copyright}"/>
    </div>
</cpp:element>
