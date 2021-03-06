<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editToolbar var="model" type="com.composum.pages.stage.model.edit.FrameModel">
    <div class="composum-pages-tools_button-group btn-group btn-group-sm" role="group">
        <cpp:treeMenu key="more" icon="navicon" label="More..." title="more folder manipulation actions...">
            <cpp:menuItem icon="copy" label="Copy" title="Copy the selected folder"
                          action="window.composum.pages.actions.folder.copy"/>
            <cpp:menuItem icon="paste" label="Paste" title="Paste element as child of the selected folder"
                          action="window.composum.pages.actions.folder.paste"/>
            <cpp:menuItem icon="id-badge" label="Rename" title="Rename the selected folder"
                          action="window.composum.pages.actions.folder.rename"/>
            <cpp:menuItem icon="arrows-alt" label="Move" title="Move the selected folder"
                          action="window.composum.pages.actions.folder.move"/>
            <cpp:menuItem icon="trash" label="Delete" title="Delete the selected folder"
                          action="window.composum.pages.actions.folder.delete"/>
        </cpp:treeMenu>
        <cpp:treeAction icon="edit" label="Rename" title="Edit folder title / type"
                        action="window.composum.pages.actions.folder.edit"/>
    </div>
    <div class="composum-pages-tools_button-group btn-group btn-group-sm" role="group">
        <cpp:treeMenu key="insert" icon="plus" label="Insert" title="insert a new content element">
            <cpp:menuItem test="${model.assetsSupport}" icon="image" label="Asset"
                          title="insert a new image asset as direct child of the selected folder"
                          action="window.composum.assets.pages.actions.asset.create"/>
            <cpp:menuItem icon="folder-open" label="Folder"
                          title="insert a new folder as direct child of the selected folder"
                          action="window.composum.pages.actions.folder.insertFolder"/>
            <cpp:menuItem icon="file-image-o" label="File"
                          title="upload a file as direct child of the selected folder"
                          action="window.composum.pages.actions.folder.insertFile"/>
        </cpp:treeMenu>
        <cpp:treeAction test="${model.assetsSupport}" icon="sliders" label="Assets Configuration"
                        title="Manage the configuration for the assets of the folder"
                        action="window.composum.assets.pages.actions.folder.config"/>
    </div>
    <div class="composum-pages-tools_button-group btn-group btn-group-smaller" role="group">
        <sling:call script="activation.jsp"/>
    </div>
</cpp:editToolbar>
