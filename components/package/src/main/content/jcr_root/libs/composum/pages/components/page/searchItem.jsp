<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<cpp:model var="page_item" type="com.composum.pages.commons.model.PageContent" scope="request"
             cssAdd="composum-pages-components-page_search-item">
    <cpn:text tagClass="${page_itemCssBase}_title" value="${page_item.title}"/>
    <cpn:text tagClass="${page_itemCssBase}_description" value="${page_item.description}"/>
</cpp:model>
