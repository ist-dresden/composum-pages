<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<cpp:element var="model" type="com.composum.pages.commons.model.LanguageRoot" mode="none"
             tagName="li" cssBase="composum-pages-components-navigation-button" cssAdd="@{modelCSS}_language"
             test="@{not empty model.alternatives}">
    <cpp:include replaceSelectors=""/>
</cpp:element>
