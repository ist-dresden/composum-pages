<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<cpp:element var="teaser" type="com.composum.pages.components.model.teaser.Teaser"
             cssAdd="@{teaserCSS}_variation_default @{teaserCSS}_link-set">
    <div class="${teaserCSS}_image">
        <cpn:link test="${teaser.hasLink}" body="true" classes="${teaserCSS}_link"
                  href="${teaser.linkUrl}" title="${cpn:text(teaser.linkTitle)}">
            <cpp:include path="image" resourceType="composum/pages/components/element/image"/>
        </cpn:link>
    </div>
    <cpn:link test="${teaser.hasLink}" body="true" classes="${teaserCSS}_link"
              href="${teaser.linkUrl}" title="${cpn:text(teaser.linkTitle)}">
        <cpp:include replaceSelectors="${teaser.textSelector}"/>
    </cpn:link>
    <cpp:include path="links" resourceType="composum/pages/components/element/link/set"/>
</cpp:element>
