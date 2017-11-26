<%@page session="false" pageEncoding="UTF-8" %><%--
--%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %><%--
--%><%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%--
--%><cpp:defineFrameObjects/>
<cpp:element var="sites" type="com.composum.pages.commons.model.Sites" mode="none"
             cssBase="composum-pages-stage-sites" cssAdd="@{sitesCssBase}_templates">
    <c:choose>
        <c:when test="${not empty sites.templates}">
            <div class="${sitesCssBase}_wrapper">
                <div class="${sitesCssBase}_toolbar">
                    <div class="${sitesCssBase}_search">
                    </div>
                </div>
                <ul class="${sitesCssBase}_list">
                    <c:forEach items="${sites.templates}" var="site">
                        <li class="${sitesCssBase}_site">
                            <input type="radio" name="template" value="${site.path}" class="${sitesCssBase}_radio"/>
                            <cpp:include resource="${site.resource}" subtype="edit/tile" replaceSelectors="list"/>
                        </li>
                    </c:forEach>
                </ul>
                <div class="${sitesCssBase}_site ${sitesCssBase}_no-template">
                    <input type="radio" name="template" value="" class="${sitesCssBase}_radio"/>
                    <sling:call script="no-template.jsp"/>
                </div>
            </div>
        </c:when>
        <c:otherwise>
            <div class="${sitesCssBase}_empty">
                <cpn:text tagClass="${sitesCssBase}_paragraph alert alert-warning"
                          value="no Site templates available" i18n="true"/>
            </div>
        </c:otherwise>
    </c:choose>
</cpp:element>
