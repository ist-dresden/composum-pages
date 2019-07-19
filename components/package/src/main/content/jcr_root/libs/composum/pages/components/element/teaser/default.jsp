<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<cpp:element var="teaser" type="com.composum.pages.components.model.teaser.Teaser"
             cssAdd="@{teaserCSS}_variation_default">
    <cpn:link test="${teaser.hasLink}" body="true" class="${teaserCSS}_link"
              href="${teaser.linkUrl}" target="${teaser.linkTarget}" title="${teaser.linkTitle}">
        <cpn:div test="${teaser.hasImage}" class="${teaserCSS}_image">
            <cpp:include path="image" resourceType="composum/pages/components/element/image"/>
        </cpn:div>
        <cpp:include replaceSelectors="${teaser.textSelector}"/>
        <cpp:include path="links" resourceType="composum/pages/components/element/link/set"/>
    </cpn:link>
</cpp:element>
