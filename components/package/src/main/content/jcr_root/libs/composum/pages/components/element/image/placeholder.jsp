<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<cpp:element var="model" type="com.composum.pages.commons.model.Image" mode="none"
             test="@{model.editMode}" cssBase="composum-pages-components-placeholder">
    <span class="fa-stack ${modelCSS}_icon">
        <i class="fa fa-picture-o fa-stack-2x"></i>
        <i class="fa fa-plus fa-stack-1x add-plus"></i>
    </span>
</cpp:element>
