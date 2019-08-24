<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:model var="model" type="com.composum.pages.stage.model.edit.FrameComponent">
    <div class="row">
        <div class="col col-xs-8">
            <cpp:widget label="Component Path" name="path" type="pathfield" value="${model.path}" required="true"/>
        </div>
        <div class="col col-xs-4">
            <cpp:widget label="Component Name" name="name" type="textfield" required="true"/>
        </div>
    </div>
    <cpp:include resourceType="composum/pages/stage/edit/default/develop/component/properties"
                 replaceSelectors="create"/>
</cpp:model>
