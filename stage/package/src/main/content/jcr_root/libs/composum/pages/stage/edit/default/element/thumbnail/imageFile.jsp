<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<cpp:model var="model" type="com.composum.pages.commons.model.Component" mode="none"
           cssBase="composum-pages-component-tile_thumbnail">
    <picture class="${modelCssBase}_picture">
        <cpn:image src="${model.thumbnail.imageRef}"/>
    </picture>
</cpp:model>
