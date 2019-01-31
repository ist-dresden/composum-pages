<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:element var="support" type="com.composum.pages.stage.model.edit.FrameModel" mode="none"
             cssClasses="composum-pages-tools">
    <div class="composum-pages-tools_actions btn-toolbar">
        <div class="composum-pages-tools_left-actions">
        </div>
        <div class="composum-pages-tools_right-actions">
            <div class="composum-pages-tools_button-group btn-group btn-group-sm" role="group">
            </div>
            <div class="composum-pages-tools_button-group btn-group btn-group-sm" role="group">
            </div>
        </div>
    </div>
    <div class="composum-pages-tools_panel">
        <div class="${supportCssBase}_support-view">
            <cpp:include subtype="edit/help"/>
        </div>
    </div>
</cpp:element>
