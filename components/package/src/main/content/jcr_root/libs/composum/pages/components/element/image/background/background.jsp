<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineObjects/>
<cpp:element var="image" type="com.composum.pages.commons.model.Image"
             test="@{image.valid||image.editMode}">
    <cpp:dropZone property="imageRef" i18n="true" filter="asset:image">
        <div class="${imageCSS}_frame">
            <c:choose>
                <c:when test="${image.valid}">
                    <div class="${imageCSS}_picture" style="background-image:url(${image.src})"
                         title="${cpn:text(image.title)}"></div>
                    <sling:call script="meta.jsp"/>
                </c:when>
                <c:otherwise>
                    <cpp:include replaceSelectors="placeholder"/>
                </c:otherwise>
            </c:choose>
        </div>
    </cpp:dropZone>
</cpp:element>
