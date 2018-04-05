<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog var="code" type="com.composum.pages.commons.model.GenericModel"
                title="Property Edit Test">
    <cpp:editDialogTab tabId="single" label="Single Values">
        <cpp:editDialogGroup label="checkboxes" expanded="true">
            <div class="row">
                <div class="col-xs-6">
                    <cpp:widget label="checkbox (1: removable)" property="single_checkbox_removable" type="checkbox"
                                hint="normal checkbox; removed if 'false'"/>
                    <cpp:widget label="checkbox (2: storing 'false')" property="single_checkbox" type="checkbox"
                                hint="toggling checkbox; 'false' if not checked" storeFalse="true"/>
                </div>
                <div class="col-xs-6">
                    <cpp:widget label="checkselect (1: removable)" property="single_checkselect_removable" type="checkselect"
                                options="checked:checked value"
                                hint="select by checkbox; 'checked' / removed"/>
                    <cpp:widget label="checkselect (2: toggeling)" property="single_checkselect" type="checkselect"
                                options="is checked,is not checked"
                                hint="toggle strings; 'is (not) checked'"/>
                </div>
            </div>
        </cpp:editDialogGroup>
        <div class="row">
            <div class="col-xs-6">
                <cpp:widget label="link (URL)" property="single_link_url" type="linkfield"
                            hint="enter an external link (http://...)"/>
            </div>
            <div class="col-xs-6">
                <cpp:widget label="link (path) " property="single_link_path" type="linkfield"
                            hint="select a repository path as link property"/>
            </div>
        </div>
        <div class="row">
            <div class="col-xs-6">
                <cpp:widget label="path " property="single_path" type="pathfield"/>
            </div>
        </div>
        <div class="row">
            <div class="col-xs-6">
                <cpp:widget label="spinner " property="single_spinner" type="numberfield"/>
            </div>
            <div class="col-xs-6">
                <cpp:widget label="numberfield " property="single_number" type="numberfield"/>
            </div>
        </div>
    </cpp:editDialogTab>
</cpp:editDialog>
