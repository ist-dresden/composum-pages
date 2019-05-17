<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog var="site" type="com.composum.pages.commons.model.Site" selector="generic" languageContext="false"
                title="Finalize Release" submitLabel="Finalize" submit="/bin/cpm/pages/release.finalize.html"
                successEvent="site:changed">
    <input name="path" type="hidden" value="${site.path}" class="${siteCssBase}_path"/>
    <div class="row">
        <div class="col col-xs-3">
            <cpp:widget name="number" label="Number" type="select" rules="mandatory"
                        options="MAJOR:major (+1._._),MINOR:minor (_.+1._),BUGFIX:bugfix (_._.+1)"/>
        </div>
        <div class="col col-xs-9">
            <cpp:widget name="title" label="Title" type="textfield"/>
        </div>
    </div>
    <div class="row">
        <div class="col col-xs-12">
            <cpp:widget name="description" label="Description" type="textarea"/>
        </div>
    </div>
</cpp:editDialog>