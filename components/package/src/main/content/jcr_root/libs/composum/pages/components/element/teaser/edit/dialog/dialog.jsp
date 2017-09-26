<%@page session="false" pageEncoding="utf-8" %><%--
--%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><cpp:defineFrameObjects/>
<cpp:editDialog var="teaser" type="com.composum.pages.components.model.teaser.Teaser"
                title="@{dialog.selector=='create'?'Create a Teaser':'Edit Teaser'}">
    <cpp:editDialogTab tabId="teaser" label="Properties">
        <cpp:widget label="Variation" property="variation" type="select" options="default,bgimage"/>
        <cpp:widget label="Link" property="link" type="link"/>
        <cpp:widget label="Title" property="title" type="text" i18n="true"/>
        <cpp:widget label="Subtitle" property="subtitle" type="text" i18n="true"/>
        <cpp:widget label="Text" property="text" type="richtext" i18n="true" mandatory="true"
                    height="200px"/>
    </cpp:editDialogTab>
    <cpp:editDialogTab tabId="image" label="Image">
        <sling:include path="image" replaceSelectors="embedded"
                       resourceType="composum/pages/components/element/image/edit/dialog"/>
    </cpp:editDialogTab>
</cpp:editDialog>
