<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<html data-context-path="${slingRequest.contextPath}">
<cpp:model var="site" type="com.composum.pages.commons.model.Site">
    <head>
        <title>"${cpn:text(site.title)}"</title>
        <sling:call script="head.jsp"/>
    </head>
    <body>
    <div class="container-fluid">
        <div class="row">
            <div class="${siteCSS} col col-xs-12">
                <h2>${cpn:text(site.title)} <span class="${siteCssBase}_title-type">(Site)</span></h2>
                <sling:call script="content.jsp"/>
            </div>
        </div>
    </div>
    <sling:call script="script.jsp"/>
    </body>
</cpp:model></html>
