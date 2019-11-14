<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<sling:defineObjects/>
<!DOCTYPE html>
<html data-context-path="${slingRequest.contextPath}">
<head>
    <sling:call script="head.jsp"/>
</head>
<body class="composum-pages-stage-preview">
<sling:call script="body.jsp"/>
<sling:call script="script.jsp"/>
</body>
</html>
