<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<div class="btn-group btn-group-sm" role="group">
    <cpp:treeMenu key="more" icon="navicon" label="More..." title="more page manipulation actions...">
        <cpp:menuItem icon="copy" label="Copy" title="Copy the selected page"
                      action="window.composum.pages.actions.page.copy"/>
        <cpp:menuItem icon="paste" label="Paste" title="Paste page as subpage of the selected page"
                      action="window.composum.pages.actions.page.paste"/>
        <cpp:menuItem icon="id-badge" label="Rename" title="rename the selected page"
                      action="window.composum.pages.actions.page.rename"/>
        <cpp:menuItem icon="arrows-alt" label="Move" title="move the selected page in the page hierarchy"
                      action="window.composum.pages.actions.page.move"/>
        <sling:call script="versionable.jsp"/>
        <cpp:menuItem icon="trash" label="Delete" title="Delete the selected page"
                      action="window.composum.pages.actions.page.delete"/>
    </cpp:treeMenu>
</div>
<cpp:treeAction icon="edit" label="Edit" title="Edit the page properties"
                action="window.composum.pages.actions.page.edit"/>
