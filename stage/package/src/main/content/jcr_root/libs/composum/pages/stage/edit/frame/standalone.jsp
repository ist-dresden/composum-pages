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
            <meta name="viewport" content="width=device-width, minimum-scale=1, maximum-scale=1, user-scalable=no, viewport-fit=cover"/>
            <meta name="format-detection" content="telephone=no">
            <cpn:clientlib type="link" category="composum.pages.edit.frame"/>
            <cpn:clientlib type="css" category="composum.pages.edit.frame"/>
            <cpn:clientlib test="${frame.developMode}" type="link" category="composum.pages.develop.frame"/>
            <cpn:clientlib test="${frame.developMode}" type="css" category="composum.pages.develop.frame"/>
        </head>
        <body class="composum-pages-${frame.displayModeHint}_body" data-path="${frame.pagePath}"
              data-pages-mode="${frame.displayMode}" data-pages-editor="standalone">
        <div class="composum-pages-stage-edit-frame_wrapper" data-path="${frame.pagePath}">
            <iframe class="composum-pages-stage-edit-frame"></iframe>
        </div>
        <div class="composum-pages-stage-edit-tools composum-widget">
            <sling:include resourceType="composum/pages/stage/edit/actions"/>
            <div class="composum-pages-stage-edit-tools_standalone">
                <div class="composum-pages-stage-edit-sidebar">
                    <div class="composum-pages-stage-edit-sidebar_handle">
                        <i class="composum-pages-stage-edit-sidebar_handle-icon fa fa-arrows-h"></i>
                        <i class="composum-pages-stage-edit-sidebar_mode-icon fa fa-eye-slash"></i>
                    </div>
                    <div class="composum-pages-stage-edit-sidebar_content">
                    </div>
                </div>
            </div>
        </div>
        <sling:call script="dialogs.jsp"/>
        <cpn:clientlib type="js" category="composum.pages.edit.frame"/>
        <cpn:clientlib test="${frame.developMode}" type="js" category="composum.pages.develop.frame"/>
        <script>
            $(document).ready(function () {
                window.composum.pages.editFrame.ready();
            });
        </script>
        </body>
        </html>
    </cpp:model>
</cpn:bundle>
