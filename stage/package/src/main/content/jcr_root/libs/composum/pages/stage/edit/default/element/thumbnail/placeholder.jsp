<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<cpp:model var="element" type="com.composum.pages.commons.model.Element" mode="none"
           cssBase="composum-pages-component-tile_thumbnail">
    <div class="${elementCssBase}_image ${elementCssBase}_placeholder fa fa-cube"></div>
</cpp:model>
