<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:model var="model" type="com.composum.pages.stage.model.edit.FrameModel">
    <i class="fa fa-circle ${modelCssBase}_icon release-status_${model.currentPage.releaseStatus.activationState}"></i>
</cpp:model>
