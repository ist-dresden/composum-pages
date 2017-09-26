<%@page session="false" pageEncoding="utf-8" %><%--
--%><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %><%--
--%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling" %><%--
--%><cpp:defineObjects/>
<cpp:element var="model" type="com.composum.pages.components.model.search.SearchResult">
    ${model.headFormatted}
    <c:if test="${model.hasError}">
        <div class="alert alert-danger" role="alert">${model.searchtermErrorText}</div>
    </c:if>
    <c:forEach items="${model.results}" var="result">
        <%-- Transfer detail information (com.composum.pages.commons.service.SearchService.Result) to renderers. --%>
        <c:set var="searchresult" value="${result}" scope="request"/>
        <c:choose>
            <c:when test="${not empty model.template}">
                <sling:include resource="${result.target}" resourceType="${model.template}"
                               replaceSelectors="${model.selector}"/>
            </c:when>
            <c:otherwise>
                <sling:include resource="${result.target}" replaceSelectors="${model.selector}"/>
            </c:otherwise>
        </c:choose>
        <c:set var="searchresult" value="${null}"/>
    </c:forEach>
    <nav aria-label="Search result pages">
        <ul class="pagination">
            <li class="page-item">
                <a class="page-link fa fa-backward" href="${model.previousSearchPage.link}" aria-label="Previous">
                    <span class="sr-only">Previous</span>
                </a>
            </li>
            <c:forEach var="searchPage" varStatus="status" items="${model.searchPages}">
                <li class="page-item ${searchPage.active ? 'active' : ''}">
                    <a class="page-link" href="${searchPage.link}"
                       title="Page ${searchPage.number}">${searchPage.number}
                        <c:if test="${searchPage.active}"><span class="sr-only">(current)</span></c:if></a></li>
            </c:forEach>
            <li class="page-item">
                <a class="page-link fa fa-forward" href="${model.nextSearchPage.link}" aria-label="Next"
                   title="Go to next page">
                    <span class="sr-only">Next</span>
                </a>
            </li>
        </ul>
    </nav>
</cpp:element>
