<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editToolbar var="model" type="com.composum.pages.stage.model.edit.FrameModel">
    <div class="composum-pages-tools_button-group btn-group btn-group-sm" role="group">
        <cpp:treeMenu key="more" icon="navicon" label="More..." title="more file manipulation actions...">
            <cpp:menuItem icon="copy" label="Copy" title="Copy the selected asset"
                          action="window.composum.pages.actions.folder.copy"/>
            <cpp:menuItem icon="id-badge" label="Rename" title="Rename the selected asset"
                          action="window.composum.pages.actions.file.rename"/>
            <cpp:menuItem icon="arrows-alt" label="Move" title="Move the selected asset"
                          action="window.composum.pages.actions.file.move"/>
            <sling:call script="versionable.jsp"/>
            <cpp:menuItem test="${model.assetsSupport}" icon="trash" label="Delete" title="Delete the selected asset"
                          action="window.composum.assets.pages.actions.asset.delete"/>
            <cpp:menuItem test="${!model.assetsSupport}" icon="trash" label="Delete" title="Delete the selected asset"
                          action="window.composum.pages.actions.file.delete"/>
        </cpp:treeMenu>
        <cpp:treeAction test="${model.assetsSupport}" icon="upload" label="Upload" title="Upload asset original"
                        action="window.composum.assets.pages.actions.asset.upload"/>
        <cpp:treeAction test="${!model.assetsSupport}" icon="upload" label="Upload" title="Upload file content"
                        action="window.composum.assets.pages.actions.file.upload"/>
    </div>
    <cpn:div test="${model.assetsSupport}"
             class="composum-pages-tools_button-group btn-group btn-group-sm" role="group">
        <cpp:treeAction icon="sliders" label="Configure" title="Configure asset"
                        action="window.composum.assets.pages.actions.asset.config"/>
    </cpn:div>
    <div class="composum-pages-tools_button-group btn-group btn-group-smaller" role="group">
        <sling:call script="activation.jsp"/>
    </div>
</cpp:editToolbar>
