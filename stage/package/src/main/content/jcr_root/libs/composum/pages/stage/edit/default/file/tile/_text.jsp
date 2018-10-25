<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:model var="file" type="com.composum.pages.commons.model.File" mode="none"
           cssBase="composum-pages-stage-file_tile">
    <div class="${fileCssBase}_text">
        <cpn:text value="${file.fileName}" tagClass="${fileCssBase}_name"></cpn:text>
        <cpn:text value="${file.mimeType}" tagClass="${fileCssBase}_mime-type"></cpn:text>
    </div>
</cpp:model>
