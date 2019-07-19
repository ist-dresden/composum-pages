<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineObjects/>
<cpp:element var="model" type="com.composum.pages.components.model.text.TextImage"
             cssAdd="@{modelCSS}_@{model.floatingText?'floating':'block'} @{modelCSS}_@{model.imagePosition}">
    <c:if test="${!model.imageBottom}">
        <div class="${modelCSS}_image">
            <cpp:include path="image" resourceType="composum/pages/components/element/image"/>
        </div>
    </c:if>
    <c:choose>
        <c:when test="${model.textValid}">
            <div class="${modelCSS}_text-block">
                <cpn:text tagName="h${model.titleLevel}" class="${modelCSS}_title"
                          value="${model.title}"/>
                <cpn:text class="${modelCSS}_text ${modelCSS}_align-${model.alignment}"
                          value="${model.text}" type="rich"/>
            </div>
        </c:when>
        <c:otherwise>
            <cpp:include replaceSelectors="placeholder"/>
        </c:otherwise>
    </c:choose>
    <c:if test="${model.imageBottom}">
        <div class="${modelCSS}_image">
            <cpp:include path="image" resourceType="composum/pages/components/element/image"/>
        </div>
    </c:if>
</cpp:element>
