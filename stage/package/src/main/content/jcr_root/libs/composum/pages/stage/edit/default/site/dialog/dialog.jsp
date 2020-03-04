<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog var="model" type="com.composum.pages.commons.model.Site" selector="generic" languageContext="false"
                title="Site Properties" submitLabel="Save">
    <cpp:editDialogTab tabId="site" label="Site">
        <cpp:widget type="hidden" name="thumbnail/image/sling:resourceType"
                    value="composum/pages/components/element/image"/>
        <div class="row">
            <div class="col col-xs-7">
                <div class="row">
                    <div class="col col-xs-6">
                        <cpp:widget label="Publish Policy" name="publicMode" type="select" default="inPlace"
                                    options="${dialog.model.publicModeOptions}"/>
                    </div>
                </div>
                <cpp:widget label="Title" property="jcr:title" type="textfield"/>
                <cpp:widget label="Homepage" property="homepage" type="pathfield"
                            hint="the path to the sites homepage (if not './home')"/>
            </div>
            <div class="col col-xs-5">
                <cpp:widget label="Thumbnail" property="thumbnail/image/imageRef" type="imagefield"
                            hint="an image path in the repository"/>
            </div>
        </div>
        <div class="row">
            <div class="col col-xs-12">
                <cpp:widget label="Description" property="jcr:description" type="richtext" height="150px"/>
            </div>
        </div>
    </cpp:editDialogTab>
    <cpp:editDialogTab tabId="languages" label="Languages">
        <cpp:include resourceType="composum/pages/stage/edit/site/languages" subtype="edit/dialog"
                     replaceSelectors="languages"/>
    </cpp:editDialogTab>
    <cpp:editDialogTab tabId="pages" label="Page Presets">
        <sling:call script="page-presets.jsp"/>
    </cpp:editDialogTab>
    <cpp:include test="${not empty model.componentSettingsEditType}"
                 resourceType="${model.componentSettingsEditType}" replaceSelectors="embedTab"/>
</cpp:editDialog>
