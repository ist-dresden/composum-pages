<%@page session="false" pageEncoding="utf-8" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><cpp:defineFrameObjects/>
<cpp:editDialog var="code" type="com.composum.pages.components.model.codeblock.CodeBlock"
                title="@{dialog.selector=='create'?'Create a Code Example':'Edit Code Example'}">
    <cpp:widget label="title" property="title" type="textfield" />
    <cpp:widget label="code" property="code" type="codearea" />
    <cpp:widget label="copyright" property="copyright" type="textfield" />
    <div class="row">
        <div class="col-xs-4">
            <cpp:widget label="Language" property="language" type="select" options=",cpp,css,groovy,html,java,javascript,jsp,php,python,ruby,scss,shell,swift,xml"/>
        </div>
        <div class="col-xs-4">
            <cpp:widget label="Language label" property="showLanguage" type="checkbox"/>
        </div>
        <div class="col-xs-4">
            <cpp:widget label="Wrap lines" property="wrapLines" type="checkbox"/>
        </div>
    </div>
    <div class="row">
        <div class="col-xs-4">
            <cpp:widget label="Border" property="bordered" type="checkbox"/>
        </div>
        <div class="col-xs-4">
            <cpp:widget label="Collapsible" property="collapsible" type="checkbox"/>
        </div>
        <div class="col-xs-4">
            <cpp:widget label="Collapsed" property="collapsed" type="checkbox"/>
        </div>
    </div>
</cpp:editDialog>
