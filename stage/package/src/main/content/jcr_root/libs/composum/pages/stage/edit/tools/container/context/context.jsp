<%@page session="false" pageEncoding="UTF-8"%>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:element var="context" type="com.composum.pages.stage.model.edit.FrameModel" mode="none"
               cssClasses="composum-pages-tools">
    <div class="composum-pages-tools_actions btn-toolbar">
        <div class="composum-pages-tools_left-actions">
            <div class="composum-pages-tools_button-group btn-group btn-group-sm" role="group">
                <button type="button"
                        class="fa fa-edit ${contextCssBase}_button-edit composum-pages-tools_button btn btn-default"
                        title="Edit Container Properties"><span
                        class="composum-pages-tools_button-label">Edit Properties</span></button>
            </div>
        </div>
        <div class="composum-pages-tools_right-actions">
    </div>
    </div>
    <div class="composum-pages-tools_panel">
        <div class="${contextCssBase}_container-context">
            <cpn:text tagName="h5" tagClass="${contextCssBase}_elements-title" i18n="true">Elements</cpn:text>
            <cpp:include resourceType="composum/pages/stage/edit/tools/container/elements"/>
        </div>
    </div>
</cpp:element>
