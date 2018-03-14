<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog var="page" type="com.composum.pages.commons.model.Page" selector="wizard" languageContext="false"
                title="Insert a new Page" submitLabel="Create" successEvent="content:inserted">
    <cpp:editDialogTab tabId="template" label="Page Template">
        <cpp:widget label="Template" name="template" type="page-template" rules="mandatory"
                    hint="select a template or type"/>
    </cpp:editDialogTab>
    <cpp:editDialogTab tabId="page" label="Page Properties">
        <cpp:widget label="Name" name="name" placeholder="the repository name" type="textfield"
                    rules="mandatory" pattern="^[A-Za-z_][- \\w]*$"/>
        <cpp:widget label="Title" name="title" placeholder="the more readable title" type="textfield"/>
        <cpp:widget label="Description" name="description" type="textarea"/>
    </cpp:editDialogTab>
</cpp:editDialog>
