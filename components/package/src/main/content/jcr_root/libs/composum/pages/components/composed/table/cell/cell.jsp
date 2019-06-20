<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineObjects/>
<cpp:element var="column" type="com.composum.pages.components.model.composed.table.Cell"
             tagName="@{column.type}" tagAttributes="@{column.tdAttributes}"
             cssAdd="valign_@{column.verticalAlign} @{column.level}">
    <c:choose>
        <c:when test="${column.hasText}">
            <cpn:text class="${columnCSS}_text align_${column.textAlign}" value="${column.text}" type="rich"/>
        </c:when>
        <c:otherwise>
            <cpp:include test="${column.editMode}" replaceSelectors="placeholder"/>
        </c:otherwise>
    </c:choose>
</cpp:element>
