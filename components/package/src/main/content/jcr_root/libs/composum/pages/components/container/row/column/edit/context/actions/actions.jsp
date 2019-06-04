<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editToolbar>
    <div class="composum-pages-tools_button-group btn-group btn-group-sm" role="group">
        <cpp:treeAction icon="plus" label="Insert Element" title="Insert a new Element"
                        action="window.composum.pages.actions.container.insert"/>
        <cpp:treeAction icon="paste" label="Paste copied Element" title="Insert a copy of an Element from the clipboard"
                        action="window.composum.pages.actions.container.paste"/>
    </div>
    <div class="composum-pages-tools_button-group btn-group btn-group-sm" role="group">
        <cpp:treeAction icon="trash" label="Delete" title="Delete the selected Column (the content)"
                        action="window.composum.pages.actions.element.delete"/>
    </div>
</cpp:editToolbar>
