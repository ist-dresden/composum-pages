<%@page session="false" pageEncoding="utf-8" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><cpp:defineFrameObjects/>
<cpp:editDialog var="code" type="com.composum.pages.components.model.codeblock.CodeBlock"
                title="@{dialog.selector=='create'?'Create a Code Example':'Edit Code Example'}">
    <cpp:widget label="title" property="title" type="text" />
    <cpp:widget label="code" property="code" type="codearea" />
    <cpp:widget label="copyright" property="copyright" type="text" />
    <div class="row">
        <div class="col-lg-4 col-md-4 col-sm-4 col-xs-12">
            <cpp:widget label="Language" property="language" type="select" options=",cpp,css,groovy,html,java,javascript,jsp,php,python,ruby,scss,shell,swift,xml"/>
        </div>
        <div class="col-lg-4 col-md-4 col-sm-4 col-xs-12">
            <cpp:widget label="Language label" property="showLanguage" type="checkbox"/>
        </div>
        <div class="col-lg-4 col-md-4 col-sm-4 col-xs-12">
            <cpp:widget label="Wrap lines" property="wrapLines" type="checkbox"/>
        </div>
    </div>
    <div class="row">
        <div class="col-lg-4 col-md-4 col-sm-4 col-xs-12">
            <cpp:widget label="Border" property="bordered" type="checkbox"/>
        </div>
        <div class="col-lg-4 col-md-4 col-sm-4 col-xs-12">
            <cpp:widget label="Collapsible" property="collapsible" type="checkbox"/>
        </div>
        <div class="col-lg-4 col-md-4 col-sm-4 col-xs-12">
            <cpp:widget label="Collapsed" property="collapsed" type="checkbox"/>
        </div>
    </div>
</cpp:editDialog>
