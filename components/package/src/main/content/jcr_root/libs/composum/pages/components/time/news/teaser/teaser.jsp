<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<cpp:element var="model" type="com.composum.pages.components.model.time.News"
             cssBase="composum-pages-components-time-news" cssAdd="@{modelCSS}_teaser">
        <div class="${modelCSS}_teaser-text">
            <h1 class="${modelCSS}_title">${cpn:text(model.title)}</h1>
            <cpn:text test="${not empty model.subtitle}" class="${modelCSS}_subtitle">${model.subtitle}</cpn:text>
            <div class="${modelCSS}_line">
                <span class="${modelCSS}_icon fa fa-rss"></span>
                <cpn:text tagName="span" class="${modelCSS}_date date"
                          value="${model.date.dateOnly ? model.date.date : model.date.dateTime}"/>
            </div>
            <cpn:text class="${modelCSS}_description" value="${model.description}" type="rich"/>
        </div>
</cpp:element>
