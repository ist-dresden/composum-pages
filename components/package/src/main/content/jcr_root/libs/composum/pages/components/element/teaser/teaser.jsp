<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<cpp:model var="teaser" type="com.composum.pages.components.model.teaser.Teaser">
    <cpp:include test="${teaser.valid}" replaceSelectors="${teaser.variation}"/>
</cpp:model>