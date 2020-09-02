<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog var="model" type="com.composum.pages.commons.model.GenericModel" languageContext="false"
                title="Insert a new File" selector="generic" submitLabel="Upload"
                submit="/bin/cpm/pages/assets.fileCreate.json@{model.path}" successEvent="content:inserted">
    <cpp:widget label="Select File" name="file" type="fileupload" required="true"/>
    <cpp:widget label="Name" name="name" placeholder="the repository name" type="textfield"
                blank="true" hint="add a name if the file name is not the right choice"
                pattern="^[\\w][\\w_-]*(\\.\\w+)?$" />
</cpp:editDialog>
