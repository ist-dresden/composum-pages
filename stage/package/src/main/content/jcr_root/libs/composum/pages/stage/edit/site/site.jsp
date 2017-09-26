<%@page session="false" pageEncoding="UTF-8"%><%--
--%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%--
--%><cpp:defineObjects/>
<html data-context-path="${slingRequest.contextPath}">
<cpp:head>
    <sling:call script="head.jsp"/>
</cpp:head>
<cpp:body var="site" type="com.composum.pages.commons.model.Site" scope="request">
    <sling:call script="content.jsp"/>
    <sling:call script="script.jsp"/>
</cpp:body>
</html>
