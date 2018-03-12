<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editToolbar>
    <div class="composum-pages-tools_button-group btn-group btn-group-sm" role="group">
        <cpp:treeAction icon="edit" label="Edit" title="Edit the site properties"
                        action="window.composum.pages.actions.site.edit"/>
    </div>
    <div class="composum-pages-tools_button-group btn-group btn-group-sm" role="group">
        <cpp:treeMenu key="insert" icon="plus" label="Insert" title="insert a new content element">
            <cpp:menuItem icon="globe" label="Page" title="Insert a new Page as direct child of the site"
                          action="window.composum.pages.actions.site.insertPage"/>
            <cpp:menuItem icon="folder-open" label="Folder" title="Insert a new Folder as direct child of the site"
                          action="window.composum.pages.actions.site.insertFolder"/>
            <cpp:menuItem icon="image" label="File" title="Upload a File as direct child of the site"
                          action="window.composum.pages.actions.site.insertFile"/>
            <cpp:menuItem icon="sitemap" label="Site" title="Create a new Site"
                          action="window.composum.pages.actions.site.create"/>
        </cpp:treeMenu>
        <cpp:treeAction icon="trash" label="Delete" title="Delete the selected site!"
                        action="window.composum.pages.actions.site.delete"/>
    </div>
</cpp:editToolbar>
