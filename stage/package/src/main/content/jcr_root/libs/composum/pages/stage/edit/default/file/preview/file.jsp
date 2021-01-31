<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<sling:defineObjects/>
<div class="composum-pages-stage-preview_file">
    <iframe class="composum-pages-stage-preview_file_frame"
            src="${slingRequest.contextPath}${cpn:path(resource.path)}" width="100%" height="100%"></iframe>
</div>
