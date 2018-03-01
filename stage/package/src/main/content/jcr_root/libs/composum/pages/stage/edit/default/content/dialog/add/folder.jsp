<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog var="folder" type="com.composum.pages.commons.model.Folder" selector="generic" languageContext="false"
                title="Insert a new Folder" submitLabel="Create">
    <div class="row">
        <div class="col-xs-8">
            <cpp:widget label="Name" name="name" placeholder="the repository name" type="textfield"
                        rules="mandatory" pattern="^[A-Za-z_][- \\w]*$"/>
        </div>
        <div class="col col-xs-4">
            <cpp:widget label="ordered" name="ordered" type="checkbox" hint="check for manual ordering"/>
        </div>
    </div>
    <cpp:widget label="Title" name="title" placeholder="the more readable title" type="textfield"/>
    <cpp:widget label="Description" name="description" type="textarea"/>
</cpp:editDialog>
