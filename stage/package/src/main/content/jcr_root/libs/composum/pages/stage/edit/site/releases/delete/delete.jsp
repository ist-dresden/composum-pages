<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog var="release" type="com.composum.pages.commons.model.SiteRelease" selector="delete"
                languageContext="false" alert-danger="Do you really want to delete this release?"
                title="Delete Release" submit="/bin/cpm/pages/release.delete.json" successEvent="site:changed">
    <div class="row">
        <div class="col col-xs-9">
            <cpp:widget label="Title" type="textfield" readonly="true" value="${release.title}"/>
        </div>
        <div class="col col-xs-3">
            <cpp:widget label="Key" type="textfield" readonly="true" value="${release.key}"/>
        </div>
    </div>
    <div class="row">
        <div class="col col-xs-12">
            <cpp:widget label="Description" type="textarea" readonly="true" value="${release.description}"/>
        </div>
    </div>
</cpp:editDialog>
