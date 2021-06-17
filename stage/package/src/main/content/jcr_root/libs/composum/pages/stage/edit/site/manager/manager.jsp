<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<cpp:defineFrameObjects/>
<cpp:model var="sites" type="com.composum.pages.commons.model.Sites"
           cssBase="composum-pages-stage-sites">
    <div class="${sitesCSS} ${sitesCSS}_manager">
        <ul class="${sitesCSS}_list">
            <c:forEach items="${sites.sites}" var="site">
                <li class="${sitesCSS}_site">
                    <input type="radio" name="select" value="${site.path}" class="${sitesCSS}_radio"/>
                    <cpp:include resource="${site.resource}" subtype="edit/tile" replaceSelectors="select" mode="none"/>
                </li>
            </c:forEach>
        </ul>
    </div>
</cpp:model>
