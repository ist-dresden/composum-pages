<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<cpp:element var="teaser" type="com.composum.pages.components.model.teaser.Teaser"
             cssAdd="@{teaserCssBase}_variation_bg-image"
             style="background-image:url(@{teaser.imageUrl})">
    <cpp:dropZone property="image/imageRef" filter="asset:image">
        <cpn:link test="${teaser.hasLink}" body="true" classes="${teaserCssBase}_link"
                  href="${teaser.linkUrl}" title="${cpn:text(teaser.linkTitle)}">
            <cpp:include replaceSelectors="${teaser.textSelector}"/>
        </cpn:link>
    </cpp:dropZone>
</cpp:element>
