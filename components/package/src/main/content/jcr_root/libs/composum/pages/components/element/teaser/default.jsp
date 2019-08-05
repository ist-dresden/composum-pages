<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<cpp:element var="teaser" type="com.composum.pages.components.model.teaser.Teaser"
             cssAdd="@{teaserCSS}_variation_default @{teaserCSS}_@{teaser.hasImage?'image':teaser.useIcon?'symbol':'text'}">
    <cpn:link test="${teaser.hasLink}" body="true" class="${teaserCSS}_link"
              href="${teaser.linkUrl}" target="${teaser.linkTarget}" title="${teaser.linkTitle}">
        <cpn:div test="${teaser.hasImage}" class="${teaserCSS}_image">
            <cpp:include path="image" resourceType="composum/pages/components/element/image"/>
        </cpn:div>
        <cpn:div test="${teaser.useIcon}" body="true" class="${teaserCSS}_icon"><i
                class="fa fa-${teaser.icon}"></i></cpn:div>
        <cpn:div test="${teaser.useIcon}" body="true" class="${teaserCSS}_text-wrapper">
            <cpp:include replaceSelectors="${teaser.textSelector}"/>
            <cpp:include path="links" resourceType="composum/pages/components/element/link/set"/>
        </cpn:div>
    </cpn:link>
</cpp:element>
