<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:model var="home" type="com.composum.pages.commons.model.PageContent" scope="request">
    <html data-context-path="${slingRequest.contextPath}">
    <head>
        <meta name="viewport" content="width=device-width, minimum-scale=1, maximum-scale=1, user-scalable=no"/>
        <meta name="format-detection" content="telephone=no">
        <cpn:text tagName="title" value="${home.properties.pageHeadline}"/>
        <cpn:clientlib type="css" category="composum.pages.stage.home"/>
    </head>
    <body class="composum-pages-stage-home">
    <div class="composum-pages-stage-home_wrapper">
        <div class="composum-pages-stage-home_top">
            <cpn:text tagName="h1" tagClass="composum-pages-stage-home_title"
                      value="${home.properties.pageHeadline}"/>
            <cpn:text tagName="h4" tagClass="composum-pages-stage-home_subtitle"
                      value="${home.properties.pageSubheadline}"/>
        </div>
        <div class="composum-pages-stage-home-sites">
            <cpn:text tagName="h2" tagClass="composum-pages-stage-home-sites_title" value="${home.title}"/>
            <cpp:include resourceType="composum/pages/stage/edit/site/list"/>
            <cpp:include resourceType="composum/pages/stage/home/tools"/>
        </div>
    </div>
    <sling:include resourceType="composum/pages/stage/edit/frame" replaceSelectors="dialogs"/>
    <cpn:clientlib type="js" category="composum.pages.stage.home"/>
    </body>
    </html>
</cpp:model>
