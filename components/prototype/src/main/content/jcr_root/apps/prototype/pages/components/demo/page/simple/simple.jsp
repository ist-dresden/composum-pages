<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<cpp:defineObjects/>
<html>
<cpp:head>
    <cpn:clientlib type="css" path="composum/pages/components/clientlibs/page"/>
</cpp:head>
<cpp:body>
    <sling:call script="main.jsp"/>
    <cpn:clientlib type="js" path="composum/pages/components/clientlibs/page"/>
</cpp:body>
</html>
