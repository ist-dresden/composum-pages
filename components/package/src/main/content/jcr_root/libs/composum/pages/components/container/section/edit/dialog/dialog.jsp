<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog var="section" type="com.composum.pages.components.model.container.Section"
                title="@{dialog.selector=='create'?'Create a Section':'Section Properties'}">
    <cpp:widget label="anchor" property="anchor" type="textfield"
                hint="the optional identifier for navigation (must be unique for a page)"/>
    <cpp:widget label="Title" property="title" type="textfield"
                hint="the optional section title"/>
    <cpp:widget label="Subitle" property="subtitle" type="textfield"
                hint="the optional section subtitle"/>
</cpp:editDialog>
