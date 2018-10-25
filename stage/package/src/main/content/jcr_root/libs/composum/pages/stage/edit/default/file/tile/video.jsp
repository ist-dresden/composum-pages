<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:model var="file" type="com.composum.pages.commons.model.File" mode="none"
           cssBase="composum-pages-stage-file_tile">
    <div class="${fileCssBase} ${file.mimeTypeCss}" draggable="true" title="${file.filePath}">
        <div class="${fileCssBase}_video-frame">
            <div class="${fileCssBase}_video-background">
                <video class="${fileCssBase}_video-player" controls>
                    <source src="${cpn:url(slingRequest,file.path)}"/>
                </video>
            </div>
        </div>
        <sling:call script="_text.jsp"/>
    </div>
</cpp:model>
