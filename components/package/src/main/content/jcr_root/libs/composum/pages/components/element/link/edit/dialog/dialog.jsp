<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog title="@{dialog.selector=='create'?'Create a Link':'Edit Link'}">
    <cpp:widget label="Link Text" property="title" type="textfield" i18n="true" hint="the label of the link"/>
    <cpp:include replaceSelectors="embedded"/>
</cpp:editDialog>
