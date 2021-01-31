<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineObjects/>
<cpp:model var="model" type="com.composum.pages.components.model.page.Sitemap">
    <ul class="${modelCSS}_list">
        <c:forEach items="${model.sitemapMenuEntries}" var="item">
            <li class="${modelCSS}_item ${modelCSS}_depth_${item.depth}"><cpn:link href="${item.loc}">
                <div class="${modelCSS}_label">${cpn:text(item.label)}</div>
                <div class="${modelCSS}_path">${cpn:text(item.path)}</div>
            </cpn:link></li>
        </c:forEach>
    </ul>
</cpp:model>
