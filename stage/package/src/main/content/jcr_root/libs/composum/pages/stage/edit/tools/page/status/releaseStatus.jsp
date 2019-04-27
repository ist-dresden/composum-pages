<%@page session="false" pageEncoding="UTF-8"%>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:model var="status" type="com.composum.pages.stage.model.edit.FramePage">
    <i class="fa fa-circle ${statusCssBase}_icon release-status_${status.releaseStatus.activationState}"></i>
</cpp:model>
