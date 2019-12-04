<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineObjects/>
<cpp:model var="calendar" type="com.composum.pages.components.model.time.AbstractCalendar" scope="request"
           cssBase="composum-pages-components-time-calendar">
    <c:forEach items="${calendar.monthRows}" var="row" varStatus="r_stat">
        <cpn:div test="${row.hasLabel}" class="${calendarCSS}_row-header">
            <cpn:div test="${calendar.showNavigation && r_stat.index==0}"
                     class="${calendarCSS}_move fa fa-chevron-left"
                     data-range="${calendar.backwardRange}"></cpn:div>
            <cpn:text class="${calendarCSS}_year" value="${row.label}"/>
            <cpn:div test="${calendar.showNavigation && r_stat.index==0}"
                     class="${calendarCSS}_move fa fa-chevron-right"
                     data-range="${calendar.forwardRange}"></cpn:div>
        </cpn:div>
        <div class="${calendarCSS}_row">
            <c:forEach items="${row.months}" var="month" varStatus="m_stat">
                <div class="${calendarCSS}_month${m_stat.first?' first':''}${m_stat.last?' last':''}">
                    <div class="${calendarCSS}_month-header" data-range="${month.monthRange}">
                        <cpn:div test="${calendar.showWeekNumbers}" class="${calendarCSS}_week-col"/>
                        <cpn:text class="${calendarCSS}_label" value="${month.label}"/>
                    </div>
                    <cpn:div test="${calendar.showWeekdayLabels}" class="${calendarCSS}_weekdays">
                        <cpn:div test="${calendar.showWeekNumbers}" class="${calendarCSS}_week-col"/>
                        <c:forEach items="${calendar.weekdayLabels}" var="weekday">
                            <cpn:text class="${calendarCSS}_weekday" value="${weekday}"/>
                        </c:forEach>
                    </cpn:div>
                    <div><%-- closed by first week --%>
                        <c:forEach items="${month}" var="day">
                        <c:if test="${day.firstDayOfWeek}"></div>
                        <%-- closing of previous week and opening next week --%>
                    <div class="${calendarCSS}_week">
                        <cpn:text test="${calendar.showWeekNumbers}" class="${calendarCSS}_week-col"
                                  value="${day.week}"/></c:if>
                        <div class="${calendarCSS}_day ${day.dayOfMonth?'inside':'outside'} ${day.hasItems?'items':'no-items'}"
                             data-range="${day.dayRange}">
                            <cpn:text class="${calendarCSS}_text" value="${day.day}"/></div>
                        </c:forEach>
                    </div>
                        <%-- closing of last week --%>
                </div>
            </c:forEach>
        </div>
    </c:forEach>
</cpp:model>
