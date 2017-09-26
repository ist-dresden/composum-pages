<%@page session="false" pageEncoding="UTF-8" %><%--
--%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%--
--%><cpp:defineFrameObjects/>
<cpp:model var="languages" type="com.composum.pages.commons.model.properties.Languages"
           cssBase="composum-pages-language-select">
    <select class="${languagesCssBase} form-control" data-default="${languages.defaultLanguage}">
        <c:forEach items="${languages.languageList}" var="language">
            <option value="${language.key}"<c:if test="${language.current}"> selected</c:if>>${language.label}
                [${language.key}]
            </option>
        </c:forEach>
    </select>
</cpp:model>
