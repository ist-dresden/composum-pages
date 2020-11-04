<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<!DOCTYPE html>
<html data-context-path="${slingRequest.contextPath}">
<cpp:head>
    <cpn:clientlib type="css" category="composum.pages.edit.frame"/>
</cpp:head>
<cpp:body>
<cpn:component var="componentPage" type="com.composum.sling.nodes.components.component.ComponentPage" scope="request">
    <cpp:include path="${componentPage.resourcePath}" resourceType="${componentPage.resourceType}" mode="edit"/>
</cpn:component>
<cpn:clientlib type="js" category="composum.pages.edit.frame"/>
</cpp:body>
</html>
