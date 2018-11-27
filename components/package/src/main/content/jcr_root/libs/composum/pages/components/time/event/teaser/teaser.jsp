<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<cpp:element var="event" type="com.composum.pages.components.model.time.Event"
             cssAdd="@{eventCssBase}_teaser">
    <div class="${eventCssBase}_tile style-${event.tileStyle}">
        <div class="${eventCssBase}_date tile-date">
            <cpn:text class="${eventCssBase}_day" value="${event.date.value}"
                      format="d" locale="${event.locale}"/>
            <cpn:text class="${eventCssBase}_month" value="${event.date.value}"
                      format="MMM" locale="${event.locale}"/>
            <cpn:text class="${eventCssBase}_time" value="${event.date.time}"/>
        </div>
        <cpn:div class="${eventCssBase}_date-end tile-date"
                 test="${not empty event.endDate.dateTime}">
            <div class="${eventCssBase}_arrow"></div>
            <cpn:text class="${eventCssBase}_day" value="${event.endDate.value}"
                      format="d" locale="${event.locale}"/>
            <cpn:text class="${eventCssBase}_month" value="${event.endDate.value}"
                      format="MMM" locale="${event.locale}"/>
            <cpn:text class="${eventCssBase}_time" value="${event.endDate.time}"/>
        </cpn:div>
    </div>
    <h1 class="${eventCssBase}_title">${cpn:text(event.title)}</h1>
    <cpn:div test="${not empty event.subtitle}"
             class="${eventCssBase}_subtitle">${cpn:text(event.subtitle)}</cpn:div>
    <div class="${eventCssBase}_line">
        <span class="${eventCssBase}_icon fa fa-calendar"></span>
        <cpn:text tagName="span" class="${eventCssBase}_date date" value="${event.date.dateTime}"/>
        <cpn:text tagName="span" class="${eventCssBase}_end-date date"
                  test="${not empty event.endDate.dateTime}"
                  value="- ${event.oneDayOnly ? event.endDate.time : event.endDate.dateTime}"/>
    </div>
    <cpn:div test="${not empty event.location}" class="${eventCssBase}_line">
        <span class="${eventCssBase}_icon fa fa-map-marker"></span>
        <cpn:link class="${eventCssBase}_location" body="true"
                  href="${event.locationUrl}">${cpn:text(event.location)}</cpn:link>
    </cpn:div>
    <cpn:text class="${eventCssBase}_text" value="${event.description}"/>
</cpp:element>
