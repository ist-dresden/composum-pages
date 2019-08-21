<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<cpp:element var="event" type="com.composum.pages.components.model.time.Event"
             cssAdd="@{eventCSS}_teaser">
    <div class="${eventCSS}_tile style-${event.tileStyle}">
        <div class="${eventCSS}_date tile-date">
            <cpn:text class="${eventCSS}_day" value="${event.date.value}"
                      format="d" locale="${event.locale}"/>
            <cpn:text class="${eventCSS}_month" value="${event.date.value}"
                      format="MMM" locale="${event.locale}"/>
            <cpn:text class="${eventCSS}_time" value="${event.date.time}"/>
        </div>
        <cpn:div class="${eventCSS}_date-end tile-date"
                 test="${not empty event.endDate.dateTime}">
            <div class="${eventCSS}_arrow"></div>
            <cpn:text class="${eventCSS}_day" value="${event.endDate.value}"
                      format="d" locale="${event.locale}"/>
            <cpn:text class="${eventCSS}_month" value="${event.endDate.value}"
                      format="MMM" locale="${event.locale}"/>
            <cpn:text class="${eventCSS}_time" value="${event.endDate.time}"/>
        </cpn:div>
    </div>
    <div class="${eventCSS}_teaser-text">
        <h1 class="${eventCSS}_title">${cpn:text(event.title)}</h1>
        <cpn:text test="${not empty event.subtitle}" class="${eventCSS}_subtitle">${event.subtitle}</cpn:text>
        <div class="${eventCSS}_line">
            <span class="${eventCSS}_icon fa fa-calendar"></span>
            <cpn:text tagName="span" class="${eventCSS}_date date"
                      value="${event.date.dateOnly ? event.date.date : event.date.dateTime}"/>
            <cpn:text tagName="span" class="${eventCSS}_end-date date"
                      test="${not empty event.endDate.dateTime}"
                      value="- ${event.oneDayOnly ? event.endDate.time : (event.endDate.dateOnly ? event.endDate.date : event.endDate.dateTime)}"/>
        </div>
        <cpn:div test="${not empty event.location}" class="${eventCSS}_line">
            <span class="${eventCSS}_icon fa fa-map-marker"></span>
            <cpn:link class="${eventCSS}_location" body="true"
                      href="${event.locationUrl}">${cpn:text(event.location)}</cpn:link>
        </cpn:div>
        <cpn:text class="${eventCSS}_description" value="${event.description}"/>
    </div>
</cpp:element>
