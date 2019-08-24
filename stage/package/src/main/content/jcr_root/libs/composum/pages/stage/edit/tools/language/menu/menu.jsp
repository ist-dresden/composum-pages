<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineFrameObjects/>
<cpp:model var="editPage" type="com.composum.pages.commons.model.Page"
           cssBase="composum-pages-language-menu">
    <ul class="${editPageCSS} dropdown-menu dropdown-menu-right" data-default="${editPage.pageLanguages.defaultLanguage}">
    <c:forEach items="${editPage.pageLanguages.languages}" var="language">
        <li class="${editPageCSS}_item"><a class="${editPageCSS}_link" href="#"
        data-value="${language.key}"><span
        class="${editPageCSS}_label">${cpn:text(language.label)}</span>
        <span class="${editPageCSS}_key">[${cpn:text(language.key)}]</span></a></li>
    </c:forEach>
    </ul>
</cpp:model>
