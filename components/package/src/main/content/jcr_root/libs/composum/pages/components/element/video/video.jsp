<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineObjects/>
<cpp:element var="model" type="com.composum.pages.components.model.asset.Video"
             test="@{model.valid||model.editMode}">
    <cpp:dropZone property="videoRef" i18n="true" filter="asset:video">
        <c:choose>
            <c:when test="${model.valid}">
                <div class="${modelCSS}_frame">
                    <sling:call script="player.jsp"/>
                </div>
                <sling:call script="meta.jsp"/>
            </c:when>
            <c:otherwise>
                <cpp:include replaceSelectors="placeholder"/>
            </c:otherwise>
        </c:choose>
    </cpp:dropZone>
</cpp:element>
