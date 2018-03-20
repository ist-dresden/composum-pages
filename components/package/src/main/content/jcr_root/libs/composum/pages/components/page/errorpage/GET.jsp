<%@page session="false" pageEncoding="utf-8"
        import="com.composum.platform.commons.request.wrapper.ErrorpageRequestWrapper"%><%
%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %><%
%><sling:defineObjects/><%
    RequestDispatcher dispatcher = slingRequest.getRequestDispatcher(resource);
    dispatcher.include(new ErrorpageRequestWrapper(slingRequest), slingResponse);
%>