<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog var="model" type="com.composum.pages.commons.model.Component"
                title="Edit Component Properties" languageContext="false">
    <cpp:widget type="checkbox" name="dialog" label="Edit Dialog"/>
    <cpp:widget type="checkbox" name="create" label="Create Dialog"/>
    <cpp:widget type="checkbox" name="delete" label="Delete Dialog"/>
    <cpp:widget type="checkbox" name="help" label="Help Page"/>
    <cpp:widget type="checkbox" name="tile" label="Component Tile"/>
    <cpp:widget type="checkbox" name="toolbar" label="Edit Toolbar"/>
    <cpp:widget type="checkbox" name="tree" label="Tree Actions"/>
    <cpp:widget type="checkbox" name="context" label="Context Actions"/>
</cpp:editDialog>
