<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog var="section" type="com.composum.pages.components.model.container.Section"
                title="@{dialog.selector=='create'?'Create a Section':'Section Properties'}">
    <cpp:widget label="anchor" property="anchor" type="textfield"
                hint="the optional identifier for navigation (must be unique for a page)"/>
    <cpp:include path="_title" resourceType="composum/pages/components/element/title" subtype="edit/dialog"
                 replaceSelectors="embedded"/>
</cpp:editDialog>
