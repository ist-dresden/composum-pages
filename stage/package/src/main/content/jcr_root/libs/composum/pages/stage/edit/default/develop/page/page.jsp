<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editToolbar>
    <div class="composum-pages-tools_button-group btn-group btn-group-sm" role="group">
        <cpp:treeMenu key="more" icon="navicon" label="More..." title="more file manipulation actions...">
            <cpp:menuItem icon="id-badge" label="Rename" title="Rename the selected page"
                          action="window.composum.pages.actions.page.rename"/>
            <cpp:menuItem icon="arrows-alt" label="Move" title="Move the selected page"
                          action="window.composum.pages.actions.page.move"/>
            <cpp:menuItem icon="trash" label="Delete" title="Delete the selected page"
                          action="window.composum.pages.actions.page.delete"/>
        </cpp:treeMenu>
        <cpp:treeAction icon="copy" label="Copy" title="Copy the selected page"
                        action="window.composum.pages.actions.page.copy"/>
    </div>
    <div class="composum-pages-tools_button-group btn-group btn-group-sm" role="group">
        <cpp:treeAction icon="edit" label="Edit" title="Open page for editing"
                        action="window.composum.pages.actions.page.open"/>
    </div>
</cpp:editToolbar>
