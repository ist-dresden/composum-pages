<%@page session="false" pageEncoding="UTF-8"%><%--
--%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%--
--%><cpp:defineFrameObjects/>
<cpp:element var="inbox" type="com.composum.pages.stage.model.edit.FrameElement" mode="none">
    <h3>Inbox (notifications)</h3>
    <p>The inbox should display a list of notifications for the current user with a resource reference.</p>
    <p>A click to this reference should display the referenced element (page) in the main page frame.</p>
</cpp:element>
