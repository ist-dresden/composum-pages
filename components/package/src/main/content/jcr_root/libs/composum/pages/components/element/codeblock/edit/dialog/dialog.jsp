<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog var="model" type="com.composum.pages.commons.model.Element"
                title="@{dialog.selector=='create'?'Create a Code View':'Edit Code View'}">
    <cpp:widget label="Title" property="title" type="textfield" i18n="true"
                hint="the title shown in the box header"/>
    <cpp:widget label="Service URI" property="serviceUri" type="textfield"
                hint="an optional URI of a servlet prepended to the reference"/>
    <cpp:widget label="Reference" property="codeRef" type="pathfield"
                hint="the resource path of an element to render or load from repository"/>
    <cpp:widget label="Code" property="code" type="codearea" language="${model.properties.language}"
                hint="the code to show (hides each reference)"/>
    <cpp:widget label="Copyright" property="copyright" type="textfield" i18n="true"
                hint="copyright notice if necessary or useful"/>
    <div class="row">
        <div class="col col-xs-4">
            <cpp:widget label="Language" property="language" type="select" i18n="false"
                        options=",cpp,css,groovy,html,java,javascript,json,jsp,php,python,ruby,scss,shell,swift,xml"/>
        </div>
        <div class="col col-xs-4">
            <cpp:widget label="Language label" property="showLanguage" type="checkbox"/>
        </div>
        <div class="col col-xs-4">
            <cpp:widget label="Wrap lines" property="wrapLines" type="checkbox"/>
        </div>
    </div>
    <div class="row">
        <div class="col col-xs-4">
            <cpp:widget label="Collapsed" property="collapsed" type="select"
                        options=",small,medium,large"/>
        </div>
        <div class="col col-xs-4">
            <cpp:widget label="Collapsible" property="collapsible" type="checkbox"/>
        </div>
        <div class="col col-xs-4">
            <cpp:widget label="Border" property="bordered" type="checkbox"/>
        </div>
    </div>
</cpp:editDialog>
