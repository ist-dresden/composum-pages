<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:model var="model" type="com.composum.pages.commons.model.File" mode="none"
           cssBase="composum-pages-stage-file_tile">
    <div class="${modelCSS} ${model.mimeTypeCss}" title="${model.path}"
         draggable="true" data-pages-edit-encoded="${model.encodedReference}">
        <div class="${modelCSS}_video-frame">
            <div class="${modelCSS}_video-background">
                <video class="${modelCSS}_video-player" controls>
                    <source src="${cpn:url(slingRequest,model.path)}"/>
                </video>
            </div>
        </div>
        <sling:call script="_text.jsp"/>
        <cpp:include resourceType="composum/pages/stage/edit/default/file/status"/>
    </div>
</cpp:model>
