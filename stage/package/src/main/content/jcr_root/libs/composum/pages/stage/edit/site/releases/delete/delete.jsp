<%@page session="false" pageEncoding="utf-8" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><cpp:defineFrameObjects/>
<cpp:editDialog var="release" type="com.composum.pages.commons.model.Release" selector="delete" languageContext="false"
                title="Delete Release" submit="/bin/cpm/pages/release.delete.html"
                alert-danger="Do you really want to delete this release?">
    <div class="row">
        <div class="col-xs-8">
            <cpp:widget label="Title" type="textfield" readonly="true" value="${release.title}"/>
        </div>
        <div class="col-xs-4">
            <cpp:widget label="Key" type="textfield" readonly="true" value="${release.key}"/>
        </div>
    </div>
    <div class="row">
        <div class="col-xs-12">
            <cpp:widget label="Descriotion" type="textarea" readonly="true" value="${release.description}"/>
        </div>
    </div>
</cpp:editDialog>
