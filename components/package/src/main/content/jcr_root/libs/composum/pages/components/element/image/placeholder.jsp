<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineObjects/>
<cpp:element var="image" type="com.composum.pages.commons.model.Image" mode="none"
             test="@{image.editMode}" cssBase="composum-pages-components-placeholder">
    <span class="fa-stack ${imageCssBase}_icon">
        <i class="fa fa-image fa-stack-2x"></i>
        <i class="fa fa-plus fa-stack-1x add-plus"></i>
    </span>
</cpp:element>
