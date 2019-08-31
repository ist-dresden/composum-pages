<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpn:bundle basename="composum-pages">
<cpp:model var="frame" type="com.composum.pages.stage.model.edit.FramePage" scope="request">
    <html data-context-path="${slingRequest.contextPath}">
    <head>
        <title>Composum Pages</title>
        <meta name="viewport" content="width=device-width, minimum-scale=1, maximum-scale=1, user-scalable=no"/>
        <meta name="format-detection" content="telephone=no">
        <cpn:clientlib type="link" category="composum.pages.browse.frame"/>
        <cpn:clientlib type="css" category="composum.pages.browse.frame"/>
    </head>
    <body class="composum-pages-${frame.displayModeHint}_body" data-path="${frame.pagePath}"
          data-pages-mode="${frame.displayMode}">
    <div class="composum-pages-stage-edit-frame_wrapper" data-path="${frame.pagePath}">
        <iframe class="composum-pages-stage-edit-frame"></iframe>
    </div>
    <div class="composum-pages-stage-edit-tools composum-widget">
        <sling:include resourceType="composum/pages/stage/edit/actions"/>
        <div class="composum-pages-stage-edit-tools_navigation">
            <div class="composum-pages-stage-edit-sidebar">
                <div class="composum-pages-stage-edit-sidebar_handle">
                    <i class="composum-pages-stage-edit-sidebar_handle-icon fa fa-arrows-h"></i>
                    <i class="composum-pages-stage-edit-sidebar_mode-icon fa fa-eye-slash"></i>
                </div>
                <div class="composum-pages-stage-edit-sidebar_content">
                    <sling:include resourceType="composum/pages/stage/edit/sidebar/navigation"/>
                </div>
            </div>
        </div>
    </div>
    <cpn:clientlib type="js" category="composum.pages.browse.frame"/>
    <script>
        $(document).ready(function () {
            window.composum.pages.editFrame.ready();
        });
    </script>
    </body>
    </html>
</cpp:model>
</cpn:bundle>