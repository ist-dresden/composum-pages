<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineObjects/>
<cpp:element var="model" type="com.composum.pages.commons.model.LanguageRoot" mode="none"
             test="@{not empty model.alternatives}">
    <button type="button" class="${modelCSS}_menu btn btn-default" data-toggle="dropdown" aria-haspopup="true"
            aria-expanded="false">${cpn:text(model.languageKeyLabel)}<span class="caret"></span></button>
    <ul class="${modelCSS}_list dropdown-menu" role="menu">
        <c:forEach items="${model.alternatives}" var="alt">
            <li class="${modelCSS}_item">
                <a class="${modelCSS}_link" href="${alt.url}" role="menuitem"
                   aria-label="${cpn:text(alt.language.label)}">${cpn:text(alt.language.label)}</a>
            </li>
        </c:forEach>
    </ul>
</cpp:element>
