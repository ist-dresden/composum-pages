<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog var="model" type="com.composum.pages.commons.model.GenericModel" languageContext="false"
                title="Insert a new File" selector="generic" resourcePath="*" cssAdd="more-width"
                submitLabel="Upload" submit="@{model.path}" successEvent="content:inserted">
    <cpp:widget type="hidden" name="*@TypeHint" value="nt:file"/>
    <div class="row">
        <div class="col col-xs-9">
            <cpp:widget label="Name" name="#name" placeholder="the repository name" type="textfield"
                        pattern="^[A-Za-z_][- \\w]*(\\.\\w+)?$" blank="true"
                        hint="if you want to paste source code select the type:"/>
        </div>
        <div class="col col-xs-3">
            <cpp:widget label="Source Type" name="#type" type="select" options=",jsp,html,js,css,xml,txt"/>
        </div>
    </div>
    <cpp:widget label="Input Source" name="#code" type="codearea" height="360px"
                hint="you can input or paste the code to upload here instead of a file upload"/>
    <cpp:widget label="Select File" name="*" type="fileupload"/>
</cpp:editDialog>
