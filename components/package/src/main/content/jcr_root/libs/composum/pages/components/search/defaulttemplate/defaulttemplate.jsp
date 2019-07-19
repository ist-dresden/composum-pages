<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<cpp:defineObjects/>
<cpp:model var="searchresult" type="com.composum.pages.commons.service.search.SearchService.Result" scope="request">
    <p>
        <cpn:link href="${searchresult.targetUrl}" class="title"><cpn:text value="${searchresult.title}"/></cpn:link>
        <span class="score">(Score ${cpn:text(searchresult.score)})</span><br/>
        <span class="excerpt">${cpn:text(searchresult.excerpt)}</span>
    </p>
</cpp:model>
