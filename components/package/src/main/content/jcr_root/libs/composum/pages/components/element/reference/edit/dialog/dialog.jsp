<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog var="model" type="com.composum.pages.components.model.reference.Reference"
                title="@{dialog.selector=='create'?'Create a Reference':'Edit Reference'}">
    <cpp:widget label="Path" property="contentReference" type="pathfield"
                hint="the path of a content resource; recursive references or references to pages are not allowed"/>
    <cpn:div test="${model.valid}" class="form-group">
        <label class="control-label composum-pages-edit-widget_label"><span
                class="label-text">${cpn:i18n(slingRequest,'Preview')}</span></label>
        <div class="widget-addon reference-preview-widget-addon" data-path="${model.path}">
            <cpn:image tagName="iframe" class="composum-pages-components-element-reference_preview-frame"
                       src="${model.path}.preview.html" width="100%" height="300px" frameBorder="0"></cpn:image>
        </div>
    </cpn:div>
</cpp:editDialog>
