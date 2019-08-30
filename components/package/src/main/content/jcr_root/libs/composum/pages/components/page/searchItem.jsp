<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineObjects/>
<cpp:model var="model" type="com.composum.pages.commons.model.Page"
           cssBase="composum-pages-components-page_search-item">
    <cpn:link href="${model.url}" class="${modelCSS}_link" body="true">
        <cpn:text class="${modelCSS}_title" value="${model.title}"/>
    </cpn:link>
    <cpp:include resourceType="composum/pages/components/navigation/breadcrumbs" replaceSelectors="search"/>
    <cpn:link test="${(searchresult != null && not empty searchresult.excerpt) || not empty model.description}"
              href="${model.url}" class="${modelCSS}_link">
        <c:choose>
            <c:when test="${searchresult != null && not empty searchresult.excerpt}">
                <cpn:text class="${modelCSS}_excerpt" value="${searchresult.excerpt}" type="rich"/>
            </c:when>
            <c:otherwise>
                <cpn:text class="${modelCSS}_description" value="${model.description}" type="rich"/>
            </c:otherwise>
        </c:choose>
    </cpn:link>
</cpp:model>
