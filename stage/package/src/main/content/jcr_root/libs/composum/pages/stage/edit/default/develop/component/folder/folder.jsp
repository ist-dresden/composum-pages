<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog var="folder" type="com.composum.pages.commons.model.Folder" selector="change"
                title="Component Folder Properties" languageContext="false">
    <cpp:widget label="Resource Type" property="sling:resourceType" type="textfield"/>
    <cpp:widget label="Resource Supertype" property="sling:resourceSuperType" type="textfield"/>
    <cpp:widget label="Title" property="jcr:title" type="textfield"/>
    <cpp:widget label="Description" property="jcr:description" type="richtext" height="100"/>
</cpp:editDialog>
