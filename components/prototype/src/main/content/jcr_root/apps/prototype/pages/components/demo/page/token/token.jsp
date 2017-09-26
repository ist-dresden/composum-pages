<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<cpp:defineObjects/>
<html>
<cpp:head>
    <cpn:clientlib type="css" path="composum/pages/components/clientlibs/page"/>
</cpp:head>
<cpp:body>
    <h1 class="hello">Hello World!</h1>
    <p>This page is sending tokens to the server on each public access to the page.</p>

    <cpp:token test="${pagesPublic}"/>

    <cpn:clientlib type="js" path="composum/pages/components/clientlibs/page"/>
</cpp:body>
</html>
