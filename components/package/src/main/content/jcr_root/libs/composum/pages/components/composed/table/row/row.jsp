<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<cpp:container var="row" type="com.composum.pages.components.model.composed.table.Row"
               tagName="tr" decoration="false" cssAdd="@{row.warningLevel}">
    <cpn:div tagName="td" test="${row.editMode}" class="${rowCSS}_handle fa fa-minus"></cpn:div>
    <sling:call script="elements.jsp"/>
    <cpn:div tagName="td" test="${row.editMode}" class="${rowCSS}_end"></cpn:div>
</cpp:container>
