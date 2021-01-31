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
