<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:element var="language" type="com.composum.pages.stage.model.edit.site.Language"
             tagName="tr">
    <td class="${languageCssBase}_name">${language.name}</td>
    <td class="${languageCssBase}_key">${language.key}</td>
    <td class="${languageCssBase}_label">${language.label}</td>
    <td class="${languageCssBase}_direction">${language.direction}</td>
</cpp:element>
