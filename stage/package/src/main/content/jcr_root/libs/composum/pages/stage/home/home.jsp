<%@page session="false" pageEncoding="UTF-8" %><%--
--%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %><%--
--%><%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><cpp:defineFrameObjects/>
<cpp:model var="home" type="com.composum.pages.commons.model.PageContent" scope="request">
    <html data-context-path="${slingRequest.contextPath}">
    <head>
        <meta name="viewport" content="width=device-width, minimum-scale=1, maximum-scale=1, user-scalable=no"/>
        <meta name="format-detection" content="telephone=no">
        <title>Composum Platform - Sites</title>
        <cpn:clientlib type="css" path="composum/pages/stage/home/clientlib"/>
    </head>
    <body class="composum-pages-stage-home">
    <div class="composum-pages-stage-home_wrapper">
        <div class="composum-pages-stage-home_top">
            <h1 class="composum-pages-stage-home_title">Composum Platform</h1>
            <h4 class="composum-pages-stage-home_subtitle">an Apache Sling Application Platform</h4>
        </div>
        <sling:include resourceType="composum/pages/stage/home/sites"/>
    </div>
    <cpn:clientlib type="js" path="composum/pages/stage/home/clientlib"/>
    </body>
    </html>
</cpp:model>
