<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineObjects/>
<cpp:element var="image" type="com.composum.pages.commons.model.Image"
             test="@{image.valid||image.editMode}" drop="asset:^image/.*$">
    <c:choose>
        <c:when test="${image.valid}">
            <div class="${imageCssBase}_frame">
                <cpn:image classes="${imageCssBase}_picture" src="${image.src}" alt="${cpn:text(image.alt)}"
                           title="${cpn:text(image.title)}"/>
            </div>
        </c:when>
        <c:otherwise>
            <cpp:include replaceSelectors="placeholder"/>
        </c:otherwise>
    </c:choose>
</cpp:element>
