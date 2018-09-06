<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editToolbar>
    <div class="composum-pages-tools_button-group btn-group btn-group-sm" role="group">
        <cpp:treeAction icon="plus" label="Create" title="Insert a new element"
                        action="window.composum.pages.actions.container.insert"/>
        <cpp:treeAction icon="paste" label="Paste" title="Paste element into the selected container"
                        action="window.composum.pages.actions.container.paste"/>
    </div>
    <div class="composum-pages-tools_button-group btn-group btn-group-sm" role="group">
        <cpp:treeAction icon="copy" label="Copy" title="Copy the selected container"
                        action="window.composum.pages.actions.element.copy"/>
        <cpp:treeAction icon="trash" label="Delete" title="Delete the selected container"
                        action="window.composum.pages.actions.container.delete"/>
    </div>
</cpp:editToolbar>
