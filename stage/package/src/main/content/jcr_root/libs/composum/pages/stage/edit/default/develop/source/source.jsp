<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editToolbar>
    <div class="composum-pages-tools_button-group btn-group btn-group-sm" role="group">
        <cpp:treeMenu key="more" icon="navicon" label="More..." title="more file manipulation actions...">
            <cpp:menuItem icon="id-badge" label="Rename" title="Rename the selected file"
                          action="window.composum.pages.actions.file.rename"/>
            <cpp:menuItem icon="arrows-alt" label="Move" title="Move the selected file"
                          action="window.composum.pages.actions.file.move"/>
            <cpp:menuItem icon="trash" label="Delete" title="Delete the selected file"
                          action="window.composum.pages.actions.file.delete"/>
        </cpp:treeMenu>
        <cpp:treeAction icon="copy" label="Copy" title="Copy the selected file"
                        action="window.composum.pages.actions.file.copy"/>
    </div>
    <div class="composum-pages-tools_button-group btn-group btn-group-sm" role="group">
        <cpp:treeAction icon="edit" label="Edit" title="Edit the selected file"
                        action="window.composum.pages.actions.component.file.editSource"/>
        <cpn:link href="/bin/cpm/edit/code.html${resource.path}" target="_blank" map="false"
                  title="${cpn:i18n(slingRequest,'Open file for editing')}"
                  class="fa fa-pencil composum-pages-tools_button btn btn-default"><span
                class="composum-pages-tools_button-label">${cpn:i18n(slingRequest,'Edit')}</span></cpn:link>
    </div>
</cpp:editToolbar>
