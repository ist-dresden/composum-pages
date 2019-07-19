<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<html data-context-path="${slingRequest.contextPath}">
<cpp:model var="model" type="com.composum.pages.stage.model.edit.site.SiteModel">
    <head>
        <title>"${cpn:text(model.site.title)}"</title>
        <sling:call script="head.jsp"/>
    </head>
    <body>
    <div class="container-fluid">
        <div class="row">
            <div class="${modelCSS} col col-xs-12">
                <h2 class="${modelCSS}_title">${cpn:text(model.site.title)}
                    <span class="${modelCSS}_title-type">(Site:${cpn:path(model.site.path)})</span></h2>
                <sling:call script="content.jsp"/>
            </div>
        </div>
    </div>
    <sling:call script="script.jsp"/>
    </body>
</cpp:model></html>
