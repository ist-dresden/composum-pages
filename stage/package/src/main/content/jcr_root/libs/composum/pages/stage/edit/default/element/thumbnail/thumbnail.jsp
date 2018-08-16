<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<cpp:model var="element" type="com.composum.pages.commons.model.Element" mode="none"
           cssBase="composum-pages-component-tile_thumbnail">
    <div class="${elementCssBase}">
        <div class="${elementCssBase}_wrapper">
            <picture class="${elementCssBase}_picture">
                <sling:call script="placeholder.jsp"/>
            </picture>
        </div>
    </div>
</cpp:model>
