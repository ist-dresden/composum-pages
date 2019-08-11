<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<div class="row">
    <div class="col col-xs-8">
        <cpp:widget label="Title" property="jcr:title" type="textfield"/>
    </div>
    <div class="col col-xs-4">
        <cpp:widget label="Primary Type" name="jcr:primaryType" type="select" required="true"
                    options="cpp:Component,sling:Folder,nt:unstructured" separators=", |"/>
    </div>
</div>
<div class="row">
    <div class="col col-xs-8">
        <cpp:widget label="Supertype" property="sling:resourceSuperType" type="textfield"/>
    </div>
    <div class="col col-xs-4">
        <cpp:widget label="Component Type" property="componentType" type="select"
                    options=",cpp:Element,cpp:Container,cpp:Page" separators=", |"/>
    </div>
</div>
<cpp:widget type="static" i18n="true" level="remark"
            value="the component type is inherited form the components supertype if not specified here"/>
<div class="row" style="align-items: flex-start;">
    <div class="col col-xs-8">
        <cpp:widget label="Description" property="jcr:description" type="textarea" rows="8"/>
    </div>
    <div class="col col-xs-4">
        <cpp:widget label="Category" property="category" type="textfield" multi="true"/>
        <cpp:widget type="static" i18n="true" level="remark"
                    value="the first category declares the group in a structured component view"/>
    </div>
</div>
