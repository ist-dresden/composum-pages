<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialogGroup label="Dialogs" expanded="true">
    <div class="row">
        <div class="col col-xs-4">
            <cpp:widget type="checkbox" name="dialog" label="Edit Dialog"/>
        </div>
        <div class="col col-xs-4">
            <cpp:widget type="checkbox" name="create" label="Create Dialog"/>
        </div>
        <div class="col col-xs-4">
            <cpp:widget type="checkbox" name="delete" label="Delete Dialog"/>
        </div>
    </div>
    <cpp:widget type="static" i18n="true" style="margin-top:-15px;"
                value="Normally a component should have an edit dialog. This dialog is used also for creation if no special 'create' dialog exists. A delete dialog can be useful if the standard delete dialog is not enough."/>
</cpp:editDialogGroup>
<cpp:editDialogGroup label="Shapes" expanded="true">
    <div class="row">
        <div class="col col-xs-4">
            <cpp:widget type="checkbox" name="tile" label="Component Tile"/>
        </div>
        <div class="col col-xs-4">
            <cpp:widget type="checkbox" name="thumbnail" label="Thumbnail"/>
        </div>
        <div class="col col-xs-4">
            <cpp:widget type="checkbox" name="help" label="Help Page"/>
        </div>
    </div>
    <cpp:widget type="static" i18n="true" style="margin-top:-15px;"
                value="The component tile is inherited from the supertype but its useful to create derived tile component and overwrite the '_icon.jsp' or '_title.jsp' scripts."/>
</cpp:editDialogGroup>
<cpp:editDialogGroup label="Actions" expanded="true">
    <div class="row">
        <div class="col col-xs-4">
            <cpp:widget type="checkbox" name="toolbar" label="Edit Toolbar"/>
        </div>
        <div class="col col-xs-4">
            <cpp:widget type="checkbox" name="tree" label="Tree Actions"/>
        </div>
        <div class="col col-xs-4">
            <cpp:widget type="checkbox" name="context" label="Context Actions"/>
        </div>
    </div>
    <cpp:widget type="static" i18n="true" style="margin-top:-15px;"
                value="The tree actions are necessary if not inherited from supertype and the component is visible in th tree. The context actions are necessary only if the context actions should be different fro the edit actions."/>
</cpp:editDialogGroup>
