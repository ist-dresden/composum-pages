<%@page session="false" pageEncoding="utf-8" %><%--
--%><%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><cpp:defineFrameObjects/>
<cpp:editToolbar>
    <div class="composum-pages-tools_button-group btn-group btn-group-sm" role="group">
        <cpp:treeAction icon="edit" label="Edit" title="Edit the site properties"
                        action="window.composum.pages.actions.site.edit"/>
    </div>
    <div class="composum-pages-tools_button-group btn-group btn-group-sm" role="group">
        <cpp:treeAction icon="plus" label="Create" title="Create a new folder, page or asset"
                        action="window.composum.pages.actions.site.insert"/>
        <cpp:treeAction icon="trash" label="Delete" title="Delete the selected site!"
                        action="window.composum.pages.actions.site.delete"/>
    </div>
</cpp:editToolbar>
