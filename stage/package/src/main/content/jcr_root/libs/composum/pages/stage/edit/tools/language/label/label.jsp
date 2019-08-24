<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:model var="languages" type="com.composum.pages.commons.model.properties.Languages"
           cssBase="composum-pages-language">
    <span class="${languagesCSS}">
        <span class="${languagesCSS}_label">${cpn:text(languages.language.label)}</span>
        <span class="${languagesCSS}_key">[${cpn:text(languages.language.key)}]</span>
    </span>
</cpp:model>
