<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineObjects/>
<cpp:model var="model" type="com.composum.pages.components.model.navigation.Languages" test="@{model.useful}">
    <c:forEach items="${model.languageList}" var="language">
        <c:if test="${not language.current}">
            <link rel="alternate" hreflang="${cpn:text(language.key)}"
                  href="${currentPage.url}?pages.locale=${cpn:text(language.key)}"/>
        </c:if>
    </c:forEach>
</cpp:model>
