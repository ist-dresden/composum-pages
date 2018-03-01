<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog var="file" type="com.composum.pages.commons.model.File" selector="generic" languageContext="false"
                title="Insert a new File" submitLabel="Upload">
    <cpp:widget label="File" name="file" type="fileupload" rules="mandatory"/>
    <cpp:widget label="Name" name="name" placeholder="the repository name" type="textfield"
                rules="blank" pattern="^[A-Za-z_][- \\w]*(\\.\\w+)?$"
                hint="add a name hint if different from the file name"/>
</cpp:editDialog>
