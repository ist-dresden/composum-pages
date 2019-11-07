<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editToolbar var="model" type="com.composum.pages.stage.model.edit.FrameModel">
    <div class="composum-pages-tools_button-group btn-group btn-group-sm" role="group">
        <div class="btn-group btn-group-sm" role="group">
            <cpp:treeMenu key="more" icon="navicon" label="More..." title="more folder manipulation actions...">
                <cpp:menuItem icon="id-badge" label="Rename" title="Rename the selected folder"
                              action="window.composum.pages.actions.folder.rename"/>
                <cpp:menuItem icon="arrows-alt" label="Move" title="Move the selected folder"
                              action="window.composum.pages.actions.folder.move"/>
            </cpp:treeMenu>
        </div>
        <cpp:treeAction icon="edit" label="Rename" title="Edit folder title / type"
                        action="window.composum.pages.actions.folder.edit"/>
    </div>
    <div class="composum-pages-tools_button-group btn-group btn-group-sm" role="group">
        <div class="btn-group btn-group-sm" role="group">
            <cpp:treeMenu key="insert" icon="plus" label="Insert" title="insert a new content element">
                <cpp:menuItem test="${model.assetsSupport}" icon="image" label="Asset" title="insert a new image asset as direct child of the selected folder"
                              action="window.composum.assets.actions.pages.asset.upload"/>
                <cpp:menuItem icon="folder-open" label="Folder"
                              title="insert a new folder as direct child of the selected folder"
                              action="window.composum.pages.actions.folder.insertFolder"/>
                <cpp:menuItem icon="file-image-o" label="File" title="upload a file as direct child of the selected folder"
                              action="window.composum.pages.actions.folder.insertFile"/>
            </cpp:treeMenu>
        </div>
        <div class="btn-group btn-group-sm" role="group">
            <cpp:treeMenu key="changes" icon="cog" label="More..." title="copy, paste or delete...">
                <cpp:menuItem icon="copy" label="Copy" title="Copy the selected folder"
                              action="window.composum.pages.actions.folder.copy"/>
                <cpp:menuItem icon="paste" label="Paste" title="Paste element as child of the selected folder"
                              condition=""
                              action="window.composum.pages.actions.folder.paste"/>
                <cpp:menuItem icon="trash" label="Delete" title="Delete the selected folder"
                              action="window.composum.pages.actions.folder.delete"/>
            </cpp:treeMenu>
        </div>
    </div>
</cpp:editToolbar>
