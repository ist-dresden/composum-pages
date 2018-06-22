<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog var="code" type="com.composum.pages.components.model.codeblock.CodeBlock"
                title="@{dialog.selector=='create'?'Create a Code View':'Edit Code View'}">
    <cpp:widget label="Title" property="title" type="textfield"
                hint="the title shown in the box header"/>
    <cpp:widget label="Service URI" property="serviceUri" type="textfield" i18n="false"
                hint="an optional URI of a servlet prepended to the reference"/>
    <cpp:widget label="Reference" property="codeRef" type="pathfield" i18n="false"
                hint="the resource path of an element to render or load from repository"/>
    <cpp:widget label="Code" property="code" type="codearea" i18n="false"
                hint="the code to show (hides each reference)"/>
    <cpp:widget label="Copyright" property="copyright" type="textfield"
                hint="copyright notice if necessary or useful"/>
    <div class="row">
        <div class="col-xs-4">
            <cpp:widget label="Language" property="language" type="select" i18n="false"
                        options=",cpp,css,groovy,html,java,javascript,json,jsp,php,python,ruby,scss,shell,swift,xml"/>
        </div>
        <div class="col-xs-4">
            <cpp:widget label="Language label" property="showLanguage" type="checkbox" i18n="false"/>
        </div>
        <div class="col-xs-4">
            <cpp:widget label="Wrap lines" property="wrapLines" type="checkbox" i18n="false"/>
        </div>
    </div>
    <div class="row">
        <div class="col-xs-4">
            <cpp:widget label="Collapsed" property="collapsed" type="select" i18n="false"
                        options=":expanded,small,medium,large"/>
        </div>
        <div class="col-xs-4">
            <cpp:widget label="Collapsible" property="collapsible" type="checkbox" i18n="false"/>
        </div>
        <div class="col-xs-4">
            <cpp:widget label="Border" property="bordered" type="checkbox" i18n="false"/>
        </div>
    </div>
</cpp:editDialog>
