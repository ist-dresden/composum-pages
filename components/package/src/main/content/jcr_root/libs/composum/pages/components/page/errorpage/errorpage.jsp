<%@page session="false" pageEncoding="utf-8"
        import="com.composum.sling.core.CoreConfiguration" %><%
%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %><%
%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%
%><cpp:defineObjects/><%
    Integer statusCode = (Integer) slingRequest.getAttribute(CoreConfiguration.ERRORPAGE_STATUS);
    if (statusCode != null) {
        // the status code must be set as request attribute by the ErrorHandler service
        slingResponse.setStatus(statusCode);
    }
    // let the page content empty if the request is an Ajax request
    if (!"XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
        slingResponse.setContentType("text/html");
        %><sling:call script="page.jsp"/><%
    }
%>