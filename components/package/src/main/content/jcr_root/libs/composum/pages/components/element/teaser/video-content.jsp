<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<cpp:model var="teaser" type="com.composum.pages.components.model.teaser.Teaser">
    <cpn:link test="${teaser.hasLink}" body="true" class="${teaserCSS}_link"
              href="${teaser.linkUrl}" target="${teaser.linkTarget}" title="${teaser.linkTitle}">
        <cpn:div test="${teaser.hasIcon}" class="${teaserCSS}_icon"><i
                class="fa fa-${teaser.icon}"></i></cpn:div>
        <cpp:include replaceSelectors="${teaser.textSelector}"/>
    </cpn:link>
</cpp:model>
