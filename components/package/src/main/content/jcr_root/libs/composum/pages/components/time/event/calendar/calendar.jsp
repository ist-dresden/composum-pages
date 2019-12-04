<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<cpp:element var="calendar" type="com.composum.pages.components.model.time.EventCalendar" scope="request"
             cssBase="composum-pages-components-time-calendar"
             cssAdd="@{calendarCSS}-event @{calendarCSS}-columns-@{calendar.columns}"
             data-path="@{calendar.path}" data-locale="@{calendar.locale}" data-detail="@{calendar.detailPage}">
    <sling:call script="panel.jsp"/>
</cpp:element>
