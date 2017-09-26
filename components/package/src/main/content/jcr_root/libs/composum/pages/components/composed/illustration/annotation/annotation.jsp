<%@page session="false" pageEncoding="UTF-8" %><%--
--%><%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="C" uri="http://java.sun.com/jsp/jstl/core" %><%--
--%><cpp:defineObjects/>
<cpp:element var="annotation" type="com.composum.pages.components.model.illustration.Annotation"
             tagId="@{annotationId}" cssAdd="@{annotation.annotationClasses}">
    <div class="${annotationCssBase}_arrow" style="${annotation.annotationArrowStyle}">
    </div>
    <div class="${annotationCssBase}_content" style="${annotation.annotationContentStyle}">
        <c:choose>
            <c:when test="${annotation.valid}">
                <cpn:text tagName="h4" tagClass="${annotationCssBase}_title" value="${annotation.title}"/>
                <cpn:text tagClass="${annotationCssBase}_text" value="${annotation.text}" type="rich"/>
                <c:if test="${annotation.hasNext}">
                    <cpn:link href="${annotation.next}" classes="${annotationCssBase}_next"
                              data-id="${annotation.nextId}"><i
                            class="${annotationCssBase}_next-icon fa fa-forward"></i></cpn:link>
                </c:if>
            </c:when>
            <c:otherwise>
                <cpp:include replaceSelectors="placeholder"/>
            </c:otherwise>
        </c:choose>
    </div>
</cpp:element>
