<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<cpp:element var="model" type="com.composum.pages.commons.model.Element">
    <div class="${modelCSS}_header">
        <cpn:text tagName="h1" value="${model.title}" i18n="true"/>
        <cpn:text tagName="h2" value="${model.properties.subtitle}" i18n="true"/>
    </div>
    <div class="${modelCSS}_text">
        <cpn:text value="${model.description}" i18n="true" type="rich"/>
    </div>
    <div class="${modelCSS}_meta">
        <cpn:text class="${modelCSS}_meta-author" value="${model.properties['meta/author']}"/>
        <cpn:text class="${modelCSS}_meta-date" value="${model.properties['meta/date']}" format="{Date}dd.MM.yyyy"/>
    </div>
</cpp:element>
