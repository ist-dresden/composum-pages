<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineObjects/>
<cpp:element var="teaser" type="com.composum.pages.components.model.teaser.Teaser"
             cssAdd="@{teaserCssBase}_variation_bg-image"
             style="background-image:url(@{teaser.imageUrl})">
    <c:if test="${teaser.hasLink}"><a class="${teaserCssBase}_link" href="${teaser.linkUrl}"></c:if>
    <cpp:include replaceSelectors="${teaser.textSelector}"/>
    <c:if test="${teaser.hasLink}"></a></c:if>
</cpp:element>
