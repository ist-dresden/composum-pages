<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog title="Insert a new Page" resourcePath="*" submitLabel="Create" successEvent="content:inserted">
    <cpp:editDialogTab tabId="template" label="Page Template">
        <cpp:widget label="Template" name="template" type="page-template" required="true"
                    hint="select a template or type"/>
    </cpp:editDialogTab>
    <cpp:editDialogTab tabId="page" label="Page Properties">
        <cpp:widget label="Name" name="name" placeholder="the repository name" type="textfield"
                    required="true" pattern="^[\\w][- \\w]*$"/>
        <cpp:widget label="Title" name="jcr:title" placeholder="the more readable title" type="textfield"/>
        <cpp:widget label="Description" name="jcr:description" type="richtext"/>
    </cpp:editDialogTab>
</cpp:editDialog>
