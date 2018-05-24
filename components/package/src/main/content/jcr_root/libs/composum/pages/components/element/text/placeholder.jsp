<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<cpp:element var="element" type="com.composum.pages.commons.model.Element"
                test="@{element.editMode}" cssBase="composum-pages-components-placeholder">
    <span class="fa-stack ${elementCssBase}_icon">
        <i class="fa fa-align-justify fa-stack-2x"></i>
        <i class="fa fa-plus fa-stack-1x add-plus"></i>
    </span>
</cpp:element>
