<%@page session="false" pageEncoding="UTF-8" %><%--
--%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%--
--%><cpp:defineFrameObjects/>
<cpp:model var="languages" type="com.composum.pages.commons.model.properties.Languages"
           cssBase="composum-pages-language">
    <span class="${languagesCssBase}">
        <span class="${languagesCssBase}_label">${languages.language.label}</span>
        <span class="${languagesCssBase}_key">[${languages.language.key}]</span>
    </span>
</cpp:model>
