<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:element var="language" type="com.composum.pages.stage.model.edit.site.Language"
             tagName="tr">
    <td class="${languageCSS}_name">${language.name}</td>
    <td class="${languageCSS}_key">${language.key}</td>
    <td class="${languageCSS}_label">${language.label}</td>
    <td class="${languageCSS}_direction">${language.direction}</td>
</cpp:element>
