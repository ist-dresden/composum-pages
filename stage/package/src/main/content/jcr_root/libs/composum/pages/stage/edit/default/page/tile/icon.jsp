<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<cpp:model var="tilepage" type="com.composum.pages.commons.model.Page" mode="none">
    <div class="composum-pages-component-tile_icon fa fa-globe release-status_${tilepage.releaseStatus.activationState}"></div>
</cpp:model>
