<%@page session="false" pageEncoding="UTF-8" %><%--
--%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %><%--
--%><%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><cpp:defineFrameObjects/>
<cpp:model var="frame" type="com.composum.pages.stage.model.edit.FramePage" scope="request">
    <html data-context-path="${slingRequest.contextPath}">
    <head>
        <cpn:clientlib type="css" path="composum/pages/stage/preview/clientlibs/frame"/>
    </head>
    <body class="composum-pages-${frame.displayModeHint}_body" data-path="${frame.pagePath}"
          data-pages-mode="PREVIEW" data-pages-locale="${frame.locale}">
    <div class="composum-pages-stage-edit-frame_wrapper" data-path="${frame.pagePath}">
        <iframe class="composum-pages-stage-edit-frame" src="${frame.pageUrl}"></iframe>
    </div>
    <div class="composum-pages-stage-edit-tools composum-widget">
        <sling:include resourceType="composum/pages/stage/edit/actions"/>
    </div>
    <cpn:clientlib type="js" path="composum/pages/stage/preview/clientlibs/frame"/>
    </body>
    </html>
</cpp:model>