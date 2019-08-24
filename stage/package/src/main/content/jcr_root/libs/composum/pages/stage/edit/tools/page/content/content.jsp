<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:container var="content" type="com.composum.pages.stage.model.edit.FramePage" mode="none"
               data-pages-edit-name="@{content.name}" data-pages-edit-path="@{content.path}"
               data-pages-edit-type="@{content.type}"
               cssAdd="composum-pages-tools">
    <div class="composum-pages-tools_actions btn-toolbar">
        <div class="composum-pages-tools_left-actions ${contentCssBase}_actions">
        </div>
        <div class="composum-pages-tools_right-actions">
        </div>
    </div>
    <div class="composum-pages-tools_panel">
        <div class="${contentCssBase}_elements-view">
            <div class="${contentCssBase}_content">
                    <%-- <cpp:include ... subtype="edit/context/elements"/> - load after init via Ajax --%>
            </div>
        </div>
    </div>
</cpp:container>
