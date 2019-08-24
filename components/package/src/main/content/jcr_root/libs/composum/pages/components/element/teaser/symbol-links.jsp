<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<cpp:element var="teaser" type="com.composum.pages.components.model.teaser.Teaser"
             cssAdd="@{teaserCSS}_variation_default @{teaserCSS}_link-set @{teaserCSS}_symbol">
    <cpn:div test="${teaser.useImage||teaser.useIcon||teaser.authorMode}" class="${teaserCSS}_icon">
        <cpp:include test="${teaser.useImage||teaser.authorMode}"
                     path="image" resourceType="composum/pages/components/element/image"/>
        <cpn:div test="${teaser.useIcon}" class="${teaserCSS}_icon"><i
                class="fa fa-${teaser.icon}"></i></cpn:div>
    </cpn:div>
    <div class="${teaserCSS}_content ${teaser.useImage?'image-symbol':''}">
        <cpn:link test="${teaser.hasLink}" body="true" class="${teaserCSS}_link"
                  href="${teaser.linkUrl}" title="${teaser.linkTitle}">
            <cpp:include replaceSelectors="${teaser.textSelector}"/>
        </cpn:link>
        <cpp:include path="links" resourceType="composum/pages/components/element/link/set"/>
    </div>
</cpp:element>
