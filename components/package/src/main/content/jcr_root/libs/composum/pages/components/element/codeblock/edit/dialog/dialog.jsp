<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog var="code" type="com.composum.pages.components.model.codeblock.CodeBlock"
                title="@{dialog.selector=='create'?'Create a Code View':'Edit Code View'}">
    <cpp:widget label="title" property="title" type="textfield"/>
    <cpp:widget label="reference" property="codeRef" type="pathfield" i18n="false"/>
    <cpp:widget label="code" property="code" type="codearea" i18n="false"/>
    <cpp:widget label="copyright" property="copyright" type="textfield"/>
    <div class="row">
        <div class="col-xs-4">
            <cpp:widget label="Language" property="language" type="select" i18n="false"
                        options=",cpp,css,groovy,html,java,javascript,jsp,php,python,ruby,scss,shell,swift,xml"/>
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
            <cpp:widget label="Border" property="bordered" type="checkbox" i18n="false"/>
        </div>
        <div class="col-xs-4">
            <cpp:widget label="Collapsible" property="collapsible" type="checkbox" i18n="false"/>
        </div>
        <div class="col-xs-4">
            <cpp:widget label="Collapsed" property="collapsed" type="checkbox" i18n="false"/>
        </div>
    </div>
</cpp:editDialog>
