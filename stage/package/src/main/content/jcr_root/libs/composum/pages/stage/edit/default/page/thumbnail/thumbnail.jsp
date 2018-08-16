<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineObjects/>
<cpp:model var="pagemodel" type="com.composum.pages.commons.model.PageContent" mode="none"
           cssBase="composum-pages-stage-page_thumbnail">
    <div class="${pagemodelCssBase}">
        <div class="${pagemodelCssBase}_wrapper">
            <picture class="${pagemodelCssBase}_picture">
                <c:choose>
                    <c:when test="${pagemodel.thumbnailAvailable}">
                        <cpp:include path="thumbnail/image"/>
                    </c:when>
                    <c:otherwise>
                        <div class="${pagemodelCssBase}_image ${pagemodelCssBase}_placeholder fa fa-globe"></div>
                    </c:otherwise>
                </c:choose>
            </picture>
        </div>
    </div>
</cpp:model>
