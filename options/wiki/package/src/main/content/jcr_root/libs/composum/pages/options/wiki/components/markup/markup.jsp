<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<cpp:defineObjects/>
<cpp:element var="model" type="com.composum.pages.options.wiki.model.Markup">
    <cpn:text tagName="h3" class="${modelCSS}_title" value="${model.title}"/>
    <cpn:div test="${not empty model.markup}"
             class="${modelCSS}_markup wikitype_${model.wikiType}">${model.markup}</cpn:div>
</cpp:element>
