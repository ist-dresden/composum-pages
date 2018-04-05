<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog var="folder" type="com.composum.pages.commons.model.Folder"
                title="Edit Folder Properties" languageContext="false">
    <div class="row">
        <div class="col-xs-8">
            <cpp:widget label="Title" property="jcr:title" type="textfield" i18n="false"/>
        </div>
        <div class="col-xs-4">
            <cpp:widget label="arrange manually" name="jcr:primaryType" type="checkselect"
                        options="sling:OrderedFolder,sling:Folder" separators=", |"
                        hint="manual sorted folder ?"/>
        </div>
    </div>
    <cpp:widget label="Description" property="jcr:description" type="richtext" i18n="false"/>
</cpp:editDialog>
