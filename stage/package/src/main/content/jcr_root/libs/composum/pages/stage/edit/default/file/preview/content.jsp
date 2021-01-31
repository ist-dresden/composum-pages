<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<sling:defineObjects/>
<cpn:component var="model" type="com.composum.pages.stage.model.preview.FileModel">
    <div class="composum-pages-stage-preview_file-header">
        <cpn:text class="composum-pages-stage-preview_path" value="${model.path}"/>
        <cpn:text class="composum-pages-stage-preview_date" value="${model.file.fileDate}"/>
    </div>
    <div class="composum-pages-stage-preview_content composum-pages-stage-preview_type-${model.fileType}">
        <sling:include path="${cpn:filter(slingRequest.requestPathInfo.suffix)}"
                       resourceType="composum/pages/stage/edit/default/file/preview"
                       replaceSelectors="${model.fileType}"/>
    </div>
</cpn:component>
