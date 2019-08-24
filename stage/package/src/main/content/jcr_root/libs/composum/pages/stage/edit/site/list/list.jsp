<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<cpp:defineFrameObjects/>
<cpp:element var="sites" type="com.composum.pages.commons.model.Sites" mode="none"
             cssBase="composum-pages-stage-sites">
    <c:choose>
        <c:when test="${not empty sites.sites}">
            <div class="${sitesCSS}_wrapper">
                <ul class="${sitesCSS}_list">
                    <c:forEach items="${sites.sites}" var="site">
                        <li class="${sitesCSS}_site">
                            <cpp:include resource="${site.resource}" subtype="edit/tile" replaceSelectors="select"/>
                        </li>
                    </c:forEach>
                </ul>
                <div class="${sitesCSS}_toolbar">
                    <div class="${sitesCSS}_search">
                    </div>
                    <div class="${sitesCSS}_actions">
                        <button class="${sitesCSS}_button ${sitesCSS}_create"><cpn:text
                                class="${sitesCSS}_label" value="${sites.properties.createSiteLabel}"/></button>
                    </div>
                </div>
            </div>
        </c:when>
        <c:otherwise>
            <div class="${sitesCSS}_no-site">
                <cpn:text tagName="p" class="${sitesCSS}_paragraph alert alert-warning"
                          value="${sites.properties.noSitesMessage}"/>
                <cpn:link href="" classes="${sitesCSS}_create alert alert-info"><cpn:text
                        tagName="span" value="${sites.properties.noSitesCreateLinkLabel}"/></cpn:link>
            </div>
        </c:otherwise>
    </c:choose>
</cpp:element>
