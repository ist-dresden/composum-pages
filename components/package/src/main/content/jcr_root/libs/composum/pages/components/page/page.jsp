<%@page session="false" pageEncoding="utf-8" import="java.util.Calendar"
%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2"
%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0"
%><cpp:defineObjects/><%
    Calendar lastModified = resource.getValueMap().get("jcr:lastModified", Calendar.class);
    if (lastModified != null) { // otherwise we have no idea when it was modified - rather don't set the header
        response.setDateHeader("Last-Modified", lastModified.getTimeInMillis());
    }
%><!DOCTYPE html>
<html ${currentPage.htmlLangAttribute} ${currentPage.htmlDirAttribute} class="${currentPage.htmlClasses}"
                                                                       data-context-path="${slingRequest.contextPath}"
                                                                       data-locale="${currentPage.locale}">
<cpp:head>
    <sling:call script="head.jsp"/>
</cpp:head>
<cpp:body cssAdd="composum-pages-components-page">
    <sling:call script="body.jsp"/>
</cpp:body>
</html>
