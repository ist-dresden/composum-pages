<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineObjects/>
<cpp:element var="model" type="com.composum.pages.commons.model.Image"
             test="@{model.valid||model.editMode}">
    <cpp:dropZone property="imageRef" i18n="true" filter="asset:image">
        <c:choose>
            <c:when test="${model.valid}">
                <div class="${modelCSS}_frame">
                    <cpn:image class="${modelCSS}_picture" src="${model.src}"
                               alt="${model.alt}" title="${model.title}"/>
                </div>
                <sling:call script="meta.jsp"/>
            </c:when>
            <c:otherwise>
                <cpp:include replaceSelectors="placeholder"/>
            </c:otherwise>
        </c:choose>
    </cpp:dropZone>
</cpp:element>
