<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineObjects/>
<cpp:element var="teaser" type="com.composum.pages.components.model.teaser.Teaser"
             tagName="none">
    <cpp:dropZone property="link" filter="page:site">
        <div class="${teaserCSS}_text-block${teaser.hasImage?'':' no-image'}">
            <cpn:text tagName="h2" class="${teaserCSS}_title" value="${teaser.title}"/>
            <cpn:text class="${teaserCSS}_subtitle" value="${teaser.subtitle}"/>
            <cpn:text type="rich" class="${teaserCSS}_text" value="${teaser.text}"/>
            <c:if test="">
                <cpp:include path="links"/>
            </c:if>
        </div>
    </cpp:dropZone>
</cpp:element>
