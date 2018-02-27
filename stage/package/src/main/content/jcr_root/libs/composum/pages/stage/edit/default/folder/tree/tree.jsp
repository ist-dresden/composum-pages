<%@page session="false" pageEncoding="utf-8" %><%--
--%><%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><cpp:defineFrameObjects/>
<cpp:editToolbar>
    <div class="composum-pages-tools_button-group btn-group btn-group-sm" role="group">
        <cpp:treeAction icon="edit" label="Rename" title="Rename the folder"
                        action="window.composum.pages.actions.folder.rename"/>
        <cpp:treeAction icon="arrows-alt" label="Move" title="Move the folder"
                        action="window.composum.pages.actions.folder.move"/>
    </div>
    <div class="composum-pages-tools_button-group btn-group btn-group-sm" role="group">
        <cpp:treeAction icon="plus" label="Create" title="Create a new folder, page or asset"
                        action="window.composum.pages.actions.folder.insert"/>
        <cpp:treeAction icon="copy" label="Copy" title="Copy the selected folder"
                        action="window.composum.pages.actions.folder.copy"/>
        <cpp:treeAction icon="paste" label="Paste" title="Paste element as child of the selected folder"
                        condition=""
                        action="window.composum.pages.actions.folder.paste"/>
        <cpp:treeAction icon="trash" label="Delete" title="Delete the selected folder"
                        action="window.composum.pages.actions.folder.delete"/>
    </div>
</cpp:editToolbar>
