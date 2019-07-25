<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog var="model" type="com.composum.pages.commons.model.Component"
                title="Edit Component Properties">
    <div class="row" style="align-items: flex-start;">
        <div class="col col-xs-8">
            <cpp:widget label="Title" property="jcr:title" type="textfield" i18n="true"/>
            <cpp:widget label="Description" property="jcr:description" type="textarea" i18n="true"/>
            <cpp:widget label="Supertype" property="sling:resourceSuperType" type="textfield"/>
        </div>
        <div class="col col-xs-4">
            <cpp:widget label="Primary Type" name="jcr:primaryType" type="select" required="true"
                        options="cpp:Component,sling:Folder,nt:unstructured" separators=", |"/>
            <cpp:widget label="Category" property="category" type="textfield" multi="true"/>
            <cpp:widget type="static" i18n="true" level="remark"
                        value="the first category declares the group in a structured component view"/>
        </div>
    </div>
</cpp:editDialog>
