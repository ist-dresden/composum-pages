<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:model var="file" type="com.composum.pages.commons.model.File" mode="none"
           cssBase="composum-pages-stage-file_tile">
    <div class="${fileCssBase}_text">
        <cpn:text value="${file.fileName}" class="${fileCssBase}_name"></cpn:text>
        <cpn:text value="${file.fileDate}" class="${fileCssBase}_date"></cpn:text>
        <cpn:text value="${file.mimeType}" class="${fileCssBase}_mime-type"></cpn:text>
        <cpn:text value="${file.filePath}" class="${fileCssBase}_path"></cpn:text>
    </div>
</cpp:model>
