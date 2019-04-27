<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:model var="model" type="com.composum.pages.stage.model.edit.FrameModel">
    <div class="${modelCssBase}" data-path="${model.frameResource.path}">
        <sling:call script="releaseStatus.jsp"/>
    </div>
</cpp:model>
