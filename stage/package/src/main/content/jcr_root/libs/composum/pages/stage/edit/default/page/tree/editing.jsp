<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<div class="btn-group btn-group-sm" role="group">
    <cpp:treeMenu key="insert" icon="plus" label="Insert" title="insert a new content element">
        <cpp:menuItem icon="globe" label="Page" title="insert a new page as direct child of the current page"
                      action="window.composum.pages.actions.page.insertPage"/>
        <cpp:menuItem icon="folder-open" label="Folder" title="insert a new folder as direct child of the current page"
                      action="window.composum.pages.actions.page.insertFolder"/>
    </cpp:treeMenu>
</div>
<div class="btn-group btn-group-sm" role="group">
    <cpp:treeMenu key="changes" icon="cog" label="More..." title="copy, paste or delete...">
        <cpp:menuItem icon="copy" label="Copy" title="Copy the selected page"
                      action="window.composum.pages.actions.page.copy"/>
        <cpp:menuItem icon="paste" label="Paste" title="Paste page as subpage of the selected page"
                      action="window.composum.pages.actions.page.paste"/>
        <cpp:menuItem icon="trash" label="Delete" title="Delete the selected page"
                      action="window.composum.pages.actions.page.delete"/>
    </cpp:treeMenu>
</div>
