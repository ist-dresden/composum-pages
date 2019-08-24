<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<cpp:model var="model" type="com.composum.pages.commons.model.Component" mode="none"
           cssBase="composum-pages-component-tile_thumbnail">
    <div class="${modelCssBase}">
        <div class="${modelCssBase}_wrapper">
            <cpp:include replaceSelectors="${model.thumbnail.type}"/>
        </div>
    </div>
</cpp:model>
