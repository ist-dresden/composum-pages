<%@page session="false" pageEncoding="UTF-8" %><%--
--%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%--
--%><cpp:defineFrameObjects/>
<cpp:model var="languages" type="com.composum.pages.commons.model.properties.Languages"
           cssBase="composum-pages-language-menu">
    <ul class="${languagesCssBase} dropdown-menu dropdown-menu-right" data-default="${languages.defaultLanguage}">
        <c:forEach items="${languages.languages}" var="language">
            <li class="${languagesCssBase}_item"><a class="${languagesCssBase}_link" href="#"
                                                    data-value="${language.key}"><span
                    class="${languagesCssBase}_label">${language.label}</span>
                <span class="${languagesCssBase}_key">[${language.key}]</span></a></li>
        </c:forEach>
    </ul>
</cpp:model>
