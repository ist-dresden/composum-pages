<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineObjects/>
<cpp:element var="cell" type="com.composum.pages.components.model.composed.table.Cell"
             tagName="@{cell.type}" tagAttributes="@{cell.tdAttributes}"
             cssAdd="valign_@{cell.verticalAlign} @{cell.level}">
    <c:choose>
        <c:when test="${cell.hasText}">
            <cpn:text tagClass="${cellCssBase}_text align_${cell.textAlign}" value="${cell.text}" type="rich"/>
        </c:when>
        <c:otherwise>
            <cpp:include test="${cell.editMode}" replaceSelectors="placeholder"/>
        </c:otherwise>
    </c:choose>
</cpp:element>
