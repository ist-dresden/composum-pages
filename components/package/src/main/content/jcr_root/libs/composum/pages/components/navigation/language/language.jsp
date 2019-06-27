<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineObjects/>
<cpp:element var="model" type="com.composum.pages.components.model.navigation.Languages" mode="none"
             test="@{model.useful}">
    <button type="button" class="${modelCSS}_menu btn btn-default" data-toggle="dropdown" aria-haspopup="true"
            aria-expanded="false">${cpn:text(model.currentKey)}<span class="caret"></span></button>
    <ul class="${modelCSS}_list dropdown-menu">
        <c:forEach items="${model.languageList}" var="language">
            <li class="${modelCSS}_item">
                <a class="${modelCSS}_link"
                   href="${currentPage.url}?pages.locale=${cpn:text(language.key)}">${cpn:text(language.label)}</a>
            </li>
        </c:forEach>
    </ul>
</cpp:element>
