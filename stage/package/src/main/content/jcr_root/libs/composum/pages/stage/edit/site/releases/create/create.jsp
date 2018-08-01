<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog var="site" type="com.composum.pages.commons.model.Site" selector="generic" languageContext="false"
                title="Create Release" submitLabel="Create" submit="/bin/cpm/pages/release.create.html"
                successEvent="site:changed">
    <input name="path" type="hidden" value="${site.path}" class="${siteCssBase}_path"/>
    <input name="objects" type="hidden" value="" class="${siteCssBase}_objects"/>
    <div class="row">
        <div class="col col-xs-8">
            <cpp:widget name="title" label="Title" type="textfield"/>
        </div>
        <div class="col col-xs-4">
            <cpp:widget name="releaseName" label="Key" type="textfield"/>
        </div>
    </div>
    <div class="row">
        <div class="col col-xs-12">
            <cpp:widget name="description" label="Description" type="textarea"/>
        </div>
    </div>
</cpp:editDialog>
