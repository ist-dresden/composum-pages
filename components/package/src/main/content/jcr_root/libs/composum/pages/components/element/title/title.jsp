<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineObjects/>
<cpp:element var="title" type="com.composum.pages.components.model.title.Title"
             style="@{title.style}" test="@{title.editMode || title.valid}">
    <cpp:dropZone property="image/imageRef" i18n="true" filter="asset:image">
        <c:choose>
            <c:when test="${title.valid}">
                <div class="${titleCssBase}_text">
                    <cpn:text tagName="h1" tagClass="${titleCssBase}_title" value="${title.title}"/>
                    <cpn:text tagName="h2" tagClass="${titleCssBase}_subtitle" value="${title.subtitle}"/>
                </div>
            </c:when>
            <c:otherwise>
                <cpp:include replaceSelectors="placeholder"/>
            </c:otherwise>
        </c:choose>
    </cpp:dropZone>
</cpp:element>
