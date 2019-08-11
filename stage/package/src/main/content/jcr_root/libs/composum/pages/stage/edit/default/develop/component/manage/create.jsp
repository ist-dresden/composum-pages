<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:model var="model" type="com.composum.pages.stage.model.edit.FrameComponent">
    <cpp:editDialogGroup label="Dialogs" expanded="true">
        <div class="row">
            <div class="col col-xs-4">
                <cpp:widget type="checkbox" name="editDialog" label="Edit Dialog" value="true"/>
            </div>
            <div class="col col-xs-4">
                <cpp:widget type="checkbox" name="createDialog" label="Create Dialog"/>
            </div>
            <div class="col col-xs-4">
                <cpp:widget type="checkbox" name="deleteDialog" label="Delete Dialog"/>
            </div>
        </div>
        <cpp:widget type="static" i18n="true" style="margin-top:-15px;"
                    value="Normally a component should have an edit dialog. This dialog is used also for creation if no special 'create' dialog exists. A delete dialog can be useful if the standard delete dialog is not enough."/>
    </cpp:editDialogGroup>
    <cpp:editDialogGroup label="Presentation" expanded="true">
        <div class="row">
            <div class="col col-xs-4">
                <cpp:widget type="checkbox" name="editTile" label="Component Tile" value="true"/>
            </div>
            <div class="col col-xs-4">
                <cpp:widget type="checkbox" name="thumbnail" label="Thumbnail"/>
            </div>
            <div class="col col-xs-4">
                <cpp:widget type="checkbox" name="helpPage" label="Help Page"/>
            </div>
        </div>
        <cpp:widget type="static" i18n="true" style="margin-top:-15px;"
                    value="The component tile is inherited from the supertype but its useful to create derived tile component and overwrite the '_icon.jsp' and/or '_title.jsp' scripts."/>
    </cpp:editDialogGroup>
    <cpp:editDialogGroup label="Actions" expanded="true">
        <div class="row">
            <div class="col col-xs-4">
                <cpp:widget type="checkbox" name="editToolbar" label="Edit Toolbar"/>
            </div>
            <div class="col col-xs-4">
                <cpp:widget type="checkbox" name="treeActions" label="Tree Actions"/>
            </div>
            <div class="col col-xs-4">
                <cpp:widget type="checkbox" name="contextActions" label="Context Actions"/>
            </div>
        </div>
        <cpp:widget type="static" i18n="true" style="margin-top:-15px;"
                    value="All actions are derived from the supertype by default. The different tree actions are useful if the supertype has it not and the component is visible in the tree. The context actions are necessary only if the context actions should be different from the edit actions."/>
    </cpp:editDialogGroup>
</cpp:model>
