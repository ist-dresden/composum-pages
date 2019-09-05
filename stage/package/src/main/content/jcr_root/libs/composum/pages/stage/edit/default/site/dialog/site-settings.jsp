<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:widget type="hidden" name="thumbnail/image/sling:resourceType" value="composum/pages/components/element/image"/>
<div class="row">
    <div class="col col-xs-7">
        <cpp:widget label="Publish Policy" name="publicMode" type="select" default="inPlace"
                    options="inPlace:In-Place replication,versions:Versions resolver,live:Live immediately"/>
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
