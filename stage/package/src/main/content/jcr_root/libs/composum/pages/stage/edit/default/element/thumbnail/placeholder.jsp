<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<cpp:model var="model" type="com.composum.pages.commons.model.Component" mode="none"
           cssBase="composum-pages-component-tile_thumbnail">
    <div class="${modelCssBase}_image ${modelCssBase}_placeholder fa fa-cube"></div>
</cpp:model>
