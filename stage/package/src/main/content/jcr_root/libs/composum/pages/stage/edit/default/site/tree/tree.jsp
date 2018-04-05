<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editToolbar>
    <div class="composum-pages-tools_button-group btn-group btn-group-sm" role="group">
        <div class="btn-group btn-group-sm" role="group">
            <cpp:treeMenu key="more" icon="navicon" label="More..." title="more page manipulation actions...">
                <cpp:menuItem icon="id-badge" label="Rename" title="Rename the selected folder"
                              action="window.composum.pages.actions.site.rename"/>
                <cpp:menuItem icon="arrows-alt" label="Move" title="Move the selected folder"
                              action="window.composum.pages.actions.site.move"/>
            </cpp:treeMenu>
        </div>
        <cpp:treeAction icon="edit" label="Edit" title="Edit the site properties"
                        action="window.composum.pages.actions.site.edit"/>
    </div>
    <div class="composum-pages-tools_button-group btn-group btn-group-sm" role="group">
        <div class="btn-group btn-group-sm" role="group">
            <cpp:treeMenu key="insert" icon="plus" label="Insert" title="insert a new content element">
                <cpp:menuItem icon="globe" label="Page" title="Insert a new Page as direct child of the site"
                              action="window.composum.pages.actions.site.insertPage"/>
                <cpp:menuItem icon="folder-open" label="Folder" title="Insert a new Folder as direct child of the site"
                              action="window.composum.pages.actions.site.insertFolder"/>
                <cpp:menuItem icon="image" label="File" title="Upload a File as direct child of the site"
                              action="window.composum.pages.actions.site.insertFile"/>
            </cpp:treeMenu>
        </div>
        <div class="btn-group btn-group-sm" role="group">
            <cpp:treeMenu key="changes" icon="cog" label="More..." title="new site, copy, paste or delete...">
                <cpp:menuItem icon="sitemap" label="Create Site" title="Create a new Site"
                              action="window.composum.pages.actions.site.create"/>
                <cpp:menuItem icon="copy" label="Copy Site" title="Copy the selected site"
                              action="window.composum.pages.actions.site.copy"/>
                <cpp:menuItem icon="paste" label="Paste into Site" title="Paste element as child of the site"
                              condition=""
                              action="window.composum.pages.actions.site.paste"/>
                <cpp:menuItem icon="trash" label="Delete Site" title="Delete the selected site!"
                              action="window.composum.pages.actions.site.delete"/>
            </cpp:treeMenu>
        </div>
    </div>
</cpp:editToolbar>
