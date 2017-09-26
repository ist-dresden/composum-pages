<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/>
<cpn:component id="image" type="libs.composum.assets.components.AdaptiveImageBean">
    <div class="adaptive image ${image.variation} ${image.rendition}">
        <figure>
            <div class="image-background">
                <img src="${image.imageUrl}">
            </div>
        </figure>
    </div>
</cpn:component>
