<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<cpp:model var="calendar" type="com.composum.pages.components.model.time.CalendarModel" scope="request">
    <sling:call script="panel.jsp"/>
</cpp:model>
