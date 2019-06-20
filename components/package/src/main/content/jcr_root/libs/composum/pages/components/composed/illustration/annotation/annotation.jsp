<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineObjects/>
<cpp:element var="annotation" type="com.composum.pages.components.model.composed.illustration.Annotation"
             tagId="@{annotationId}">
    <c:choose>
        <c:when test="${annotation.valid}">
            <h4 class="${annotationCSS}_title">
                <i class="${annotationCSS}_icon ${annotation.iconClasses}">${cpn:text(annotation.shapeText)}</i>
                    ${cpn:text(annotation.title)}</h4>
            <cpn:text class="${annotationCSS}_text" value="${annotation.text}" type="rich"/>
        </c:when>
        <c:otherwise>
            <cpp:include replaceSelectors="placeholder"/>
        </c:otherwise>
    </c:choose>
</cpp:element>
