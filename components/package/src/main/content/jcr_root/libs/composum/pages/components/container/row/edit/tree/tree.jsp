<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editToolbar>
    <div class="composum-pages-tools_button-group btn-group btn-group-sm" role="group">
        <cpp:treeAction icon="edit" label="Edit" title="Edit the selected Element"
                        action="window.composum.pages.actions.element.edit"/>
        <cpp:treeAction icon="trash" label="Delete" title="Delete the selected Element"
                        action="window.composum.pages.actions.element.delete"/>
    </div>
</cpp:editToolbar>
