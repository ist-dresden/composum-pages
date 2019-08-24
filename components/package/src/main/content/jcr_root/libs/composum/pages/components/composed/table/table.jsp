<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<cpp:container var="table" type="com.composum.pages.components.model.composed.table.Table" decoration="false">
    <cpn:text class="${tableCSS}_title composum-pages-components-subtitle" value="${table.title}"/>
    <div class="${tableCSS}_wrapper composum-pages-components-wrapper">
        <table class="${tableCSS}_table ${table.tableClasses}">
            <cpn:div tagName="tr" test="${table.editMode}" class="${tableCSS}_handle fa fa-table"></cpn:div>
            <sling:call script="elements.jsp"/>
            <cpn:div tagName="tr" test="${table.editMode}" class="${tableCSS}_end"></cpn:div>
        </table>
    </div>
    <cpn:text class="${tableCSS}_copyright composum-pages-components-copyright" value="${table.copyright}"/>
</cpp:container>
