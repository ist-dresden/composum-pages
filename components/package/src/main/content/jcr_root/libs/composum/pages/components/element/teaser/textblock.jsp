<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineObjects/>
<cpp:element var="teaser" type="com.composum.pages.components.model.teaser.Teaser"
             tagName="none">
    <cpp:dropZone property="link" filter="page:site">
        <div class="${teaserCssBase}_text-block">
            <cpn:text tagName="h2" tagClass="${teaserCssBase}_title" value="${teaser.title}"/>
            <cpn:text tagName="h3" tagClass="${teaserCssBase}_subtitle" value="${teaser.subtitle}"/>
            <cpn:text type="rich" tagClass="${teaserCssBase}_text" value="${teaser.text}"/>
            <c:if test="">
                <cpp:include path="links"/>
            </c:if>
        </div>
    </cpp:dropZone>
</cpp:element>
