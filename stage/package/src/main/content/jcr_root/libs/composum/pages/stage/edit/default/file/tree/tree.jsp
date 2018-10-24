<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editToolbar>
    <div class="composum-pages-tools_button-group btn-group btn-group-sm" role="group">
        <div class="btn-group btn-group-sm" role="group">
            <cpp:treeMenu key="more" icon="navicon" label="More..." title="more file manipulation actions...">
                <cpp:menuItem icon="id-badge" label="Rename" title="Rename the selected file"
                              action="window.composum.pages.actions.file.rename"/>
                <cpp:menuItem icon="arrows-alt" label="Move" title="Move the selected file"
                              action="window.composum.pages.actions.file.move"/>
            </cpp:treeMenu>
        </div>
        <cpp:treeAction icon="upload" label="Upload" title="Upload new file content"
                        action="window.composum.pages.actions.file.upload"/>
    </div>
    <div class="composum-pages-tools_button-group btn-group btn-group-sm" role="group">
        <cpp:treeAction icon="copy" label="Copy" title="Copy the selected file"
                        action="window.composum.pages.actions.file.copy"/>
        <cpp:treeAction icon="trash" label="Delete" title="Delete the selected file"
                        action="window.composum.pages.actions.file.delete"/>
    </div>
</cpp:editToolbar>
