<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog var="model" type="com.composum.pages.commons.model.GenericModel" languageContext="false"
                title="Insert a new Folder" selector="generic" submitLabel="Create"
                submit="/bin/cpm/pages/develop.createPath.json@{model.path}">
    <cpp:widget name="jcr:primaryType" type="hidden"/>
    <cpp:widget name="#ordered" type="hidden"/><%-- both hidden fields are neccessary in used JS code --%>
    <cpp:widget label="Name or Path" name="path" placeholder="the repository path to create" type="textfield"
                required="true" pattern="^[A-Za-z_][\\w /-]*$"/>
</cpp:editDialog>
