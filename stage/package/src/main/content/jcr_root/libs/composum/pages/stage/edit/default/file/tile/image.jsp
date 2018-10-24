<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:model var="file" type="com.composum.pages.commons.model.File" mode="none"
           cssBase="composum-pages-stage-file_tile">
    <div class="${fileCssBase} ${file.mimeTypeCss}" draggable="true">
        <div class="${fileCssBase}_image-frame">
            <div class="${fileCssBase}_image-background"
                 style="background-image:url(${cpn:unmappedUrl(slingRequest,'/libs/composum/nodes/console/browser/images/image-background-dk.png')})">
                <cpn:image classes="${fileCssBase}_image" src="${file.path}" draggable="false"/>
            </div>
        </div>
        <sling:call script="_text.jsp"/>
    </div>
</cpp:model>
