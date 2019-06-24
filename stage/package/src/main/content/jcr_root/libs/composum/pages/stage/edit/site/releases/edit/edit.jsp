<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog var="release" type="com.composum.pages.commons.model.SiteRelease" selector="change"
                title="Edit Release" submit="/bin/cpm/pages/release.change.json" successEvent="site:changed">
    <div class="row">
        <div class="col col-xs-9">
            <cpp:widget name="title" label="Title" type="textfield" value="${release.title}"/>
        </div>
        <div class="col col-xs-3">
            <cpp:widget label="Key" type="textfield" value="${release.key}" disabled="true"/>
        </div>
    </div>
    <div class="row">
        <div class="col col-xs-12">
            <cpp:widget name="description" label="Description" type="textarea" value="${release.description}"/>
        </div>
    </div>
</cpp:editDialog>
