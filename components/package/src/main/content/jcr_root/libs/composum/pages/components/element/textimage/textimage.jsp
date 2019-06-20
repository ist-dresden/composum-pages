<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineObjects/>
<cpp:element var="textImage" type="com.composum.pages.components.model.text.TextImage"
             cssAdd="@{textImageCSS}_@{textImage.floatingText?'floating':'block'} @{textImageCSS}_@{textImage.imagePosition}">
    <c:if test="${!textImage.imageBottom}">
        <div class="${textImageCSS}_image">
            <cpp:include path="image" resourceType="composum/pages/components/element/image"/>
        </div>
    </c:if>
    <c:choose>
        <c:when test="${textImage.textValid}">
            <div class="${textImageCSS}_text-block">
                <cpn:text tagName="h${textImage.titleLevel}" class="${textImageCSS}_title"
                          value="${textImage.title}"/>
                <cpn:text class="${textImageCSS}_text ${textImageCSS}_align-${textImage.alignment}"
                          value="${textImage.text}" type="rich"/>
            </div>
        </c:when>
        <c:otherwise>
            <cpp:include replaceSelectors="placeholder"/>
        </c:otherwise>
    </c:choose>
    <c:if test="${textImage.imageBottom}">
        <div class="${textImageCSS}_image">
            <cpp:include path="image" resourceType="composum/pages/components/element/image"/>
        </div>
    </c:if>
</cpp:element>
