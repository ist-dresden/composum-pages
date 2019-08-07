<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog var="model" type="com.composum.pages.commons.model.Component"
                title="Edit Component Properties" languageContext="false">
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
    <div class="row">
        <div class="col col-xs-4">
            <cpp:widget type="checkbox" name="help" label="Help Page"/>
        </div>
        <div class="col col-xs-4">
            <cpp:widget type="checkbox" name="tile" label="Component Tile"/>
        </div>
    </div>
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
</cpp:editDialog>
