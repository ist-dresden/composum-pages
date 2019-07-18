<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editToolbar>
    <div class="composum-pages-tools_button-group btn-group btn-group-sm" role="group">
        <cpp:treeMenu key="more" icon="navicon" label="More..." title="more file manipulation actions...">
            <cpp:menuItem icon="id-badge" label="Rename" title="Rename the selected component"
                          action="window.composum.pages.actions.folder.rename"/>
            <cpp:menuItem icon="arrows-alt" label="Move" title="Move the selected component"
                          action="window.composum.pages.actions.folder.move"/>
            <cpp:menuItem icon="trash" label="Delete" title="Delete the selected component"
                          action="window.composum.pages.actions.folder.delete"/>
        </cpp:treeMenu>
        <cpp:treeAction icon="copy" label="Copy" title="Copy the selected component"
                        action="window.composum.pages.actions.component.copy"/>
    </div>
    <div class="composum-pages-tools_button-group btn-group btn-group-sm" role="group">
        <div class="btn-group btn-group-sm" role="group">
            <cpp:treeAction icon="edit" label="Edit" title="Edit component properties"
                            action="window.composum.pages.actions.component.edit"/>
            <cpp:treeMenu key="insert" icon="puzzle-piece" label="Add" title="add / open component pieces">
                <cpp:menuItem icon="globe" label="Page" title="insert a new page as direct child of the selected folder"
                              action="window.composum.pages.actions.folder.insertPage"/>
                <cpp:menuItem icon="folder-open" label="Folder"
                              title="insert a new folder as direct child of the selected folder"
                              action="window.composum.pages.actions.folder.insertFolder"/>
                <cpp:menuItem icon="image" label="File" title="upload a file as direct child of the selected folder"
                              action="window.composum.pages.actions.folder.insertFile"/>
            </cpp:treeMenu>
        </div>
    </div>
</cpp:editToolbar>
