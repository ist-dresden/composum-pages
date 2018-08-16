<%@page session="false" pageEncoding="UTF-8" %><%
%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %><%
%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%
%><cpp:defineObjects/><%
    if (true) throw new RuntimeException("this page.jsp can't be used without the right selector: " + resource + " - " + slingRequest.getRequestPathInfo());
%>
<html>
<sling:include replaceSelectors="head-start"/>
<sling:include replaceSelectors="head-end"/>
<sling:include replaceSelectors="body-start"/>
<sling:include replaceSelectors="body-end"/>
</html>
