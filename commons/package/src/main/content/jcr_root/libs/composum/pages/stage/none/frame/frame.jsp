<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:model var="frame" type="com.composum.pages.stage.model.edit.FramePage" scope="request">
    <%
        frame.redirectToPage();
    %>
</cpp:model>