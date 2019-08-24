<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<cpp:defineObjects/>
<cpp:model var="searchresult" type="com.composum.pages.commons.service.search.SearchService.Result" scope="request"
           cssBase="composum-pages-components-page_search-item">
    <cpn:link href="${searchresult.targetUrl}" class="${searchresultCSS}_link" body="true">
        <cpn:text value="${searchresult.title}"/>
    </cpn:link>
    <cpp:include path="${searchresult.target.path}" replaceSelectors="search"
                 resourceType="composum/pages/components/navigation/breadcrumbs"/>
    <cpn:link test="${not empty searchresult.excerpt}"
              href="${searchresult.targetUrl}" class="${searchresultCSS}_link">
        <span class="${searchresultCSS}_excerpt">${cpn:rich(slingRequest,searchresult.excerpt)}</span>
    </cpn:link>
</cpp:model>
