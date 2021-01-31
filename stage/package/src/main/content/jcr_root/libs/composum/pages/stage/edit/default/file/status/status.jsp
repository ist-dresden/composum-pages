<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:model var="model" type="com.composum.pages.stage.model.edit.FrameAsset" mode="none"
           cssBase="composum-pages-stage-file-status">
    <cpn:div test="${model.versionable}" class="${modelCssBase}" data-path="${model.frameResource.path}">
        <i class="fa fa-circle ${modelCssBase}_icon release-status_${model.releaseStatus.activationState}"></i>
    </cpn:div>
</cpp:model>
