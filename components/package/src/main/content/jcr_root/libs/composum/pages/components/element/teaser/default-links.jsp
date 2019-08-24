<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<cpp:element var="teaser" type="com.composum.pages.components.model.teaser.Teaser"
             cssAdd="@{teaserCSS}_variation_default @{teaserCSS}_link-set @{teaserCSS}_@{teaser.shape}">
    <cpn:div test="${!teaser.noAsset||teaser.authorMode}" class="${teaserCSS}_asset">
        <cpn:link test="${teaser.hasLink&&!teaser.noAsset}" body="true" class="${teaserCSS}_link"
                  href="${teaser.linkUrl}" title="${teaser.linkTitle}">
            <cpn:div test="${teaser.useImage||teaser.authorMode}" class="${teaserCSS}_image">
                <cpp:include path="image" resourceType="composum/pages/components/element/image"/>
            </cpn:div>
            <cpn:div test="${teaser.useVideo||teaser.authorMode}" class="${teaserCSS}_video">
                <cpp:include path="video" resourceType="composum/pages/components/element/video"/>
            </cpn:div>
        </cpn:link>
    </cpn:div>
    <cpn:div test="${teaser.useIcon}" class="${teaserCSS}_icon"><i
            class="fa fa-${teaser.icon}"></i></cpn:div>
    <div class="${teaserCSS}_content">
        <cpn:link test="${teaser.hasLink}" body="true" class="${teaserCSS}_link"
                  href="${teaser.linkUrl}" title="${teaser.linkTitle}">
            <cpp:include replaceSelectors="${teaser.textSelector}"/>
        </cpn:link>
        <cpp:include path="links" resourceType="composum/pages/components/element/link/set"/>
    </div>
</cpp:element>
