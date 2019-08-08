<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editToolbar>
    <div class="composum-pages-tools_button-group btn-group btn-group-sm" role="group">
        <div class="btn-group btn-group-sm" role="group">
            <cpp:treeMenu key="more" icon="navicon" label="More..." title="more folder manipulation actions...">
                <cpp:menuItem icon="paste" label="Paste" title="Paste element as child of the selected folder"
                              condition=""
                              action="window.composum.pages.actions.folder.paste"/>
                <cpp:menuItem icon="id-badge" label="Rename" title="Rename the selected folder"
                              action="window.composum.pages.actions.folder.rename"/>
                <cpp:menuItem icon="arrows-alt" label="Move" title="Move the selected folder"
                              action="window.composum.pages.actions.folder.move"/>
                <cpp:menuItem icon="trash" label="Delete" title="Delete the selected folder"
                              action="window.composum.pages.actions.folder.delete"/>
            </cpp:treeMenu>
            <cpp:treeAction icon="copy" label="Copy" title="Copy the selected folder"
                          action="window.composum.pages.actions.folder.copy"/>
        </div>
    </div>
    <div class="composum-pages-tools_button-group btn-group btn-group-sm" role="group">
        <cpp:treeAction icon="edit" label="Rename" title="Edit folder title / type"
                        action="window.composum.pages.actions.component.folder.edit"/>
        <div class="btn-group btn-group-sm" role="group">
            <cpp:treeMenu key="insert" icon="plus" label="Insert" title="insert a new content element">
                <cpp:menuItem icon="puzzle-piece" label="Component" title="create a new component"
                              action="window.composum.pages.actions.component.create"/>
                <cpp:menuItem icon="image" label="File" title="upload a file as direct child of the selected folder"
                              action="window.composum.pages.actions.folder.insertFile"/>
                <cpp:menuItem icon="folder-open" label="Folder"
                              title="insert a new folder as direct child of the selected folder"
                              action="window.composum.pages.actions.folder.insertFolder"/>
            </cpp:treeMenu>
        </div>
    </div>
</cpp:editToolbar>
