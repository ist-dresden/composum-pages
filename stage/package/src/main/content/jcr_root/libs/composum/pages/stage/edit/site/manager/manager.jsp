<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<cpp:defineFrameObjects/>
<cpp:model var="sites" type="com.composum.pages.commons.model.Sites"
           cssBase="composum-pages-stage-sites">
    <div class="${sitesCssBase} ${sitesCssBase}_manager">
        <ul class="${sitesCssBase}_list">
            <c:forEach items="${sites.sites}" var="site">
                <li class="${sitesCssBase}_site">
                    <input type="radio" name="select" value="${site.path}" class="${sitesCssBase}_radio"/>
                    <cpp:include resource="${site.resource}" subtype="edit/tile" replaceSelectors="select"/>
                </li>
            </c:forEach>
        </ul>
    </div>
</cpp:model>
