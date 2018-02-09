<%@page session="false" pageEncoding="UTF-8" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%--
--%><cpp:defineObjects/>
<cpp:model var="site" type="com.composum.pages.commons.model.SiteConfiguration" mode="none"
           cssBase="composum-pages-stage-site_thumbnail">
    <div class="${siteCssBase}">
        <div class="${siteCssBase}_wrapper">
            <picture class="${siteCssBase}_picture">
                <c:choose>
                    <c:when test="${site.thumbnailAvailable}">
                        <cpp:include path="thumbnail/image"/>
                    </c:when>
                    <c:otherwise>
                        <div class="${siteCssBase}_image ${siteCssBase}_placeholder fa fa-globe"></div>
                    </c:otherwise>
                </c:choose>
            </picture>
        </div>
    </div>
</cpp:model>
