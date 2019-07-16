<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog var="code" type="com.composum.pages.commons.model.GenericModel"
                title="Property Edit Test">
    <cpp:editDialogTab tabId="single" label="Single Value">
        <cpp:editDialogGroup label="checkboxes" expanded="true">
            <div class="row">
                <div class="col col-xs-6">
                    <cpp:widget label="checkbox (1: removable)" property="single_checkbox_removable" type="checkbox"
                                hint="normal checkbox; removed if 'false'"/>
                    <cpp:widget label="checkbox (2: storing 'false')" property="single_checkbox" type="checkbox"
                                hint="toggling checkbox; 'false' if not checked" storeFalse="true"/>
                </div>
                <div class="col col-xs-6">
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
            <div class="col col-xs-6">
                <cpp:widget label="link (URL)" property="single_link_url" type="linkfield"
                            hint="enter an external link (http://...)"/>
            </div>
            <div class="col col-xs-6">
                <cpp:widget label="link (path) " property="single_link_path" type="linkfield"
                            hint="select a repository path as link property"/>
            </div>
        </div>
        <div class="row">
            <div class="col col-xs-6">
                <cpp:widget label="path " property="single_path" type="pathfield"/>
            </div>
        </div>
        <div class="row">
            <div class="col col-xs-6">
                <cpp:widget type="textfield" label="Text Field" property="single_text" i18n="true"
                            rules="blank" pattern="^[a-zA-Z_.-]+@[a-zA-Z_.-]+$"
                            pattern-hint="a value matching pattern: '{}'"
                            hint="an email like text<br/>(pattern: '^[a-zA-Z_.-]+@[a-zA-Z_.-]+$')"/>
            </div>
            <div class="col col-xs-6">
                <cpp:widget type="email" label="E-Mail Field" property="single_mail" i18n="true"
                            rules="blank" hint="an email address (RFC 2822)"/>
            </div>
        </div>
        <div class="row">
            <div class="col col-xs-6">
            </div>
            <div class="col col-xs-6">
                <cpp:widget type="slider" label="Slider" property="single_slider" i18n="true" options="1.5;5;0.5;2"
                            hint="a slider can not have an empty<br/>or undefined value (options: '1.5;5;0.5;2')"/>
            </div>
        </div>
        <div class="row">
            <div class="col col-xs-6">
                <cpp:widget label="spinner " property="single_spinner" type="numberfield"/>
            </div>
            <div class="col col-xs-6">
                <cpp:widget label="numberfield " property="single_number" type="numberfield"/>
            </div>
        </div>
        <div class="row">
            <div class="col col-xs-6">
                <cpp:widget type="select" label="Select" property="single_select" i18n="true"
                            options="opt1:Option #1,opt2:Option #2,:nothing"/>
                <cpp:widget type="radio" label="Radio" property="single_radio" i18n="true"
                            options="opt1:Option #1,opt2:Option #2,:nothing"/>
                <cpp:widget type="radiolist" label="Radio List" property="single_radiolist" i18n="true"
                            options="opt1:Option #1,opt2:Option #2,:nothing"/>
            </div>
            <div class="col col-xs-6">
                <cpp:widget type="dimension" label="Dimension" property="single_dimension" i18n="true"
                            hint="width & height"/>
                <cpp:widget type="position" label="Position" property="single_position"
                            hint="coordinates: x, y"/>
            </div>
        </div>
    </cpp:editDialogTab>
    <cpp:editDialogTab tabId="multivalue" label="Multi Value">
        <cpp:widget type="hidden" name="languages/sling:resourceType" value="composum/pages/stage/edit/site/languages"/>
        <cpp:multiwidget label="Languages" property="languages"
                         modelClass="com.composum.pages.commons.model.properties.Languages"
                         var="language" cssAdd="multiwidget-table">
            <div class="row">
                <cpp:widget type="hidden" name="sling:resourceType" value="composum/pages/stage/edit/site/languages/language"/>
                <div class="col col-xs-4">
                    <cpp:widget label="Name" name=":name" value="${language.name}" type="textfield"/>
                </div>
                <div class="col col-xs-2">
                    <cpp:widget label="Key" property="key" type="textfield"/>
                </div>
                <div class="col col-xs-4">
                    <cpp:widget label="Label" property="label" type="textfield"/>
                </div>
                <div class="col col-xs-2">
                    <cpp:widget label="Dir" name="direction" value="${language.direction}" type="select"
                                options=",ltr,rtl"/>
                </div>
            </div>
        </cpp:multiwidget>
        <div class="row">
            <div class="col col-xs-5">
                <cpp:widget label="Multi Test 1" property="multitest1" multi="true" type="select" i18n="true"
                            options=",key:label,key-2:--2--,c:=c=,d,e"/>
            </div>
            <div class="col col-xs-7">
                <cpp:widget label="Multi Test 2" property="multitest2" multi="true" type="slider"
                            hint="a slider can not have an empty or undefined value"/>
            </div>
        </div>
        <cpp:multiwidget label="Multi Table" property="path/to/child/multitest3" var="multi" i18n="true"
                         cssAdd="multiwidget-table">
            <div class="row">
                <div class="col col-xs-3">
                    <cpp:widget label="Label" name="label" value="${multi.label}" type="textfield"
                                pattern="[a-z]+" pattern-hint="ein paar Buchstaben" rules="required,blank"/>
                </div>
                <div class="col col-xs-2">
                    <cpp:widget label="active" property="active" type="checkbox"/>
                </div>
                <div class="col col-xs-2">
                    <cpp:widget label="Type" property="type" type="select" options="foo,bar"/>
                </div>
                <div class="col col-xs-5">
                    <cpp:widget label="Weight" property="weight" type="slider"
                                hint="a slider can not have an empty or undefined value"/>
                </div>
            </div>
        </cpp:multiwidget>
    </cpp:editDialogTab>
    <cpp:editDialogTab tabId="text" label="Text/Rich/Code">
        <cpp:widget type="richtext" label="Rich Text" property="single_richtext" i18n="true"/>
        <cpp:widget type="codearea" label="Code Area" property="single_groovy" i18n="true" language="groovy"
                    hint="a Groovy script"/>
        <cpp:widget type="textarea" label="Text Area" property="single_plaintext" i18n="true"/>
    </cpp:editDialogTab>
    <cpp:editDialogTab tabId="image" label="Image">
        <cpp:widget type="imagefield" label="Image" property="single_image" i18n="true"/>
    </cpp:editDialogTab>
</cpp:editDialog>
