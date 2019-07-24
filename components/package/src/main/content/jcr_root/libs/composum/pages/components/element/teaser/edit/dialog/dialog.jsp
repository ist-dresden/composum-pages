<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog var="teaser" type="com.composum.pages.components.model.teaser.Teaser"
                title="@{dialog.selector=='create'?'Create a Teaser':'Edit Teaser'}">
    <cpp:editDialogTab tabId="teaser" label="Properties">
        <div class="row">
            <div class="col col-xs-8">
                <cpp:widget label="Teaser Title" property="title" type="textfield" i18n="true"/>
            </div>
            <div class="col col-xs-4">
                <cpp:widget label="Variation" property="variation" type="select" hint="render type"
                            options="default,bgimage" default="default"/>
            </div>
        </div>
        <cpp:widget label="Subtitle" property="subtitle" type="textfield" i18n="true"/>
        <cpp:widget label="Text" property="text" type="richtext" i18n="true" required="true"
                    height="200px"/>
        <cpp:include resourceType="composum/pages/components/element/link" subtype="edit/dialog"
                     replaceSelectors="embedded"/>
    </cpp:editDialogTab>
    <cpp:editDialogTab tabId="image" label="Image">
        <cpp:include path="image" resourceType="composum/pages/components/element/image" subtype="edit/dialog"
                     replaceSelectors="embedded"/>
    </cpp:editDialogTab>
</cpp:editDialog>
