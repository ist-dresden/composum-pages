<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<cpp:element var="model" type="com.composum.pages.components.model.text.Popup"
             test="@{model.valid||model.editMode}" cssAdd="@{modelCSS}_align-@{model.alignment}">
    <a type="button" class="${modelCSS}_link btn btn-default"
       data-trigger="focus" tabindex="0" role="button"
       data-toggle="popover" data-container="body" data-placement="${model.placement}"
       title="${model.title}" data-content='${cpn:attr(slingRequest,model.text,1)}'>
            ${cpn:text(model.linkText)}
    </a>
</cpp:element>
