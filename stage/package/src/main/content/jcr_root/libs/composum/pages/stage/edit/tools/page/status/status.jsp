<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:model var="status" type="com.composum.pages.stage.model.edit.FramePage">
    <div class="${statusCssBase}" data-path="${status.path}">
        <sling:call script="releaseStatus.jsp"/>
    </div>
</cpp:model>
