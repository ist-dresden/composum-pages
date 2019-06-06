<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<cpp:model var="page_item" type="com.composum.pages.commons.model.Page"
           cssBase="composum-pages-components-page">
    <div class="${page_itemCssBase}_search-item">
        <cpn:link href="${page_item.url}" body="true">
            <cpn:text class="${page_itemCssBase}_title" value="${page_item.title}"/>
            <cpn:text class="${page_itemCssBase}_description" value="${page_item.description}"/>
        </cpn:link>
    </div>
</cpp:model>
