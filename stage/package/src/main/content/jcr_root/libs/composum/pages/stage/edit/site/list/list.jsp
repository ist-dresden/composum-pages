<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<cpp:defineFrameObjects/>
<cpp:element var="sites" type="com.composum.pages.commons.model.Sites" mode="none"
             cssBase="composum-pages-stage-sites">
    <c:choose>
        <c:when test="${not empty sites.sites}">
            <div class="${sitesCssBase}_wrapper">
                <ul class="${sitesCssBase}_list">
                    <c:forEach items="${sites.sites}" var="site">
                        <li class="${sitesCssBase}_site">
                            <cpp:include resource="${site.resource}" subtype="edit/tile" replaceSelectors="select"/>
                        </li>
                    </c:forEach>
                </ul>
                <div class="${sitesCssBase}_toolbar">
                    <div class="${sitesCssBase}_search">
                    </div>
                    <div class="${sitesCssBase}_actions">
                        <button class="${sitesCssBase}_button ${sitesCssBase}_create"><cpn:text
                                tagClass="${sitesCssBase}_label" value="${sites.properties.createSiteLabel}"/></button>
                    </div>
                </div>
            </div>
        </c:when>
        <c:otherwise>
            <div class="${sitesCssBase}_no-site">
                <cpn:text tagName="p" tagClass="${sitesCssBase}_paragraph alert alert-warning"
                          value="${sites.properties.noSitesMessage}"/>
                <cpn:link href="" classes="${sitesCssBase}_create alert alert-info"><cpn:text
                        tagName="span" value="${sites.properties.noSitesCreateLinkLabel}"/></cpn:link>
            </div>
        </c:otherwise>
    </c:choose>
</cpp:element>
