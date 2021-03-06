<%@page session="false" pageEncoding="utf-8" %>
<%--
  a dialog to upload new file content (change file)
--%>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog var="model" type="com.composum.pages.commons.model.GenericModel" languageContext="false"
                title="Upload new file Content" selector="generic" submitLabel="Upload"
                submit="/bin/cpm/pages/assets.fileUpdate.json@{model.path}" successEvent="content:changed">
    <cpp:widget label="Path" type="textfield" value="${model.path}" disabled="true"/>
    <cpp:widget label="Select File" name="file" type="fileupload" required="true"/>
</cpp:editDialog>
