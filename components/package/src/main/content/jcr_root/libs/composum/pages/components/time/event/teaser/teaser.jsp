<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<cpp:element var="model" type="com.composum.pages.components.model.time.Event"
             cssBase="composum-pages-components-time-event" cssAdd="@{modelCSS}_teaser">
    <cpn:link test="${model.hasLink}" body="true" class="${modelCSS}_link"
              href="${model.linkUrl}" title="${model.linkTitle}" target="${model.linkTarget}">
        <div class="${modelCSS}_tile style-${model.tileStyle}">
            <div class="${modelCSS}_date tile-date">
                <cpn:text class="${modelCSS}_day" value="${model.date.value}"
                          format="d" locale="${model.locale}"/>
                <cpn:text class="${modelCSS}_month" value="${model.date.value}"
                          format="MMM" locale="${model.locale}"/>
                <cpn:text test="${model.date.showTime}" class="${modelCSS}_time" value="${model.date.time}"/>
            </div>
            <cpn:div class="${modelCSS}_date-end tile-date"
                     test="${not empty model.endDate.dateTime}">
                <div class="${modelCSS}_arrow"></div>
                <cpn:text class="${modelCSS}_day" value="${model.endDate.value}"
                          format="d" locale="${model.locale}"/>
                <cpn:text class="${modelCSS}_month" value="${model.endDate.value}"
                          format="MMM" locale="${model.locale}"/>
                <cpn:text test="${model.endDate.showTime}" class="${modelCSS}_time" value="${model.endDate.time}"/>
            </cpn:div>
        </div>
        <div class="${modelCSS}_teaser-text">
            <h1 class="${modelCSS}_title">${cpn:text(model.title)}</h1>
            <cpn:text test="${not empty model.subtitle}" class="${modelCSS}_subtitle">${model.subtitle}</cpn:text>
            <div class="${modelCSS}_line">
                <span class="${modelCSS}_icon fa fa-calendar"></span>
                <cpn:text tagName="span" class="${modelCSS}_date date"
                          value="${model.date.dateOnly ? model.date.date : model.date.dateTime}"/>
                <cpn:text tagName="span" class="${modelCSS}_end-date date"
                          test="${not empty model.endDate.dateTime}"
                          value="- ${model.oneDayOnly ? model.endDate.time : (model.endDate.dateOnly ? model.endDate.date : model.endDate.dateTime)}"/>
            </div>
            <cpn:div test="${not empty model.location}" class="${modelCSS}_line">
                <span class="${modelCSS}_icon fa fa-map-marker"></span>
                <cpn:link class="${modelCSS}_location" body="true"
                          href="${model.locationUrl}">${cpn:text(model.location)}</cpn:link>
            </cpn:div>
            <cpn:text class="${modelCSS}_description" value="${model.description}" type="rich"/>
        </div>
    </cpn:link>
</cpp:element>
