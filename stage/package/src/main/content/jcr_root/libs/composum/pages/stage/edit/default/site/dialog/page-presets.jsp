<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialogGroup label="Page Styles" expanded="true">
    <div class="row">
        <div class="col col-xs-6">
            <cpp:widget label="Page View Styles" property="style/category.view" type="textfield"
                        hint="the clientlib category for the normal and public page rendering"/>
        </div>
        <div class="col col-xs-6">
            <cpp:widget label="Edit Styles" property="style/category.edit" type="textfield"
                        hint="the additional clientlib category for the style modifications during the page editing"/>
        </div>
    </div>
</cpp:editDialogGroup>
