<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:widget type="hidden" name="logo/sling:resourceType" value="composum/pages/components/element/image"/>
<div class="row">
    <div class="col col-xs-6">
        <cpp:widget label="Logo Image" property="logo/imageRef" type="imagefield"/>
    </div>
    <div class="col col-xs-6">
        <cpp:widget label="Theme" property="style/category.theme" type="textfield"
                    hint="the additional clientlib category of the pages theme (optional)"/>
        <cpp:widget label="Page View Styles" property="style/category.view" type="textfield"
                    hint="the clientlib category for the normal and public page rendering"/>
        <cpp:widget label="Edit Styles" property="style/category.edit" type="textfield"
                    hint="the additional clientlib category for the style modifications during the page editing"/>
    </div>
</div>
