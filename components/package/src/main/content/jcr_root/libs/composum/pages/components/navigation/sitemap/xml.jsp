<?xml version="1.0" encoding="UTF-8"?>
<%@page session="false" pageEncoding="utf-8" contentType="text/xml" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineObjects/>
<urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">
    <cpp:model var="sitemap" type="com.composum.pages.components.model.navigation.Sitemap">
        <c:if test="${not empty sitemap.entries}">
            <c:forEach items="${sitemap.entries}" var="item">
                <url>
                    <loc><c:out value="${item.loc}"></c:out></loc>
                    <c:if test="${not empty item.lastMod}">
                        <lastmod><c:out value="${item.lastMod}"></c:out></lastmod>
                    </c:if>
                </url>
            </c:forEach>
        </c:if>
    </cpp:model>
</urlset>
