<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<cpp:element var="model" type="com.composum.pages.components.model.navigation.Languages" mode="none"
             tagName="li" cssBase="composum-pages-components-navigation-button" cssAdd="@{modelCSS}_language"
             test="@{model.useful}">
    <cpp:include replaceSelectors=""/>
</cpp:element>
