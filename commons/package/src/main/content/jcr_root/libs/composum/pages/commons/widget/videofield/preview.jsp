<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<cpp:defineFrameObjects/>
<div class="${widgetCSS}_preview">
    <div class="${widgetCSS}_wrapper">
        <div class="${widgetCSS}_frame"
             style="background-image:url(${cpn:unmappedUrl(slingRequest,'/libs/composum/nodes/commons/images/image-background.png')})">
            <sling:call script="player.jsp"/>
        </div>
    </div>
    <div class="${widgetCSS}_data"></div>
</div>

