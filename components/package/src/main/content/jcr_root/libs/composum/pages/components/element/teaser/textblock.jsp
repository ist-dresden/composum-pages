<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineObjects/>
<cpp:element var="model" type="com.composum.pages.components.model.teaser.Teaser"
             tagName="none">
    <cpp:dropZone property="link" filter="page:site">
        <div class="${modelCSS}_text-block${model.noAsset?' no-asset':''}">
            <cpn:text tagName="${model.titleTagName}" class="${modelCSS}_title" value="${model.title}"/>
            <cpn:text class="${modelCSS}_subtitle" value="${model.subtitle}"/>
            <cpn:text type="rich" class="${modelCSS}_text" value="${model.text}"/>
        </div>
    </cpp:dropZone>
</cpp:element>
