<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:model var="model" type="com.composum.pages.commons.model.File" mode="none"
           cssBase="composum-pages-stage-file_tile">
    <div class="${modelCSS} ${model.mimeTypeCss}" title="${model.path}">
        <div class="${modelCSS}_frame">
            <div class="${modelCSS}_properties">
                <cpn:text value="${model.name}" class="${modelCSS}_name"></cpn:text>
                <cpn:text value="${model.mimeType}" class="${modelCSS}_mime-type"></cpn:text>
            </div>
        </div>
    </div>
</cpp:model>
