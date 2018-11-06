<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<cpp:defineFrameObjects/>
<div class="${widgetCssBase}_preview">
    <div class="${widgetCssBase}_wrapper">
        <div class="${widgetCssBase}_frame"
             style="background-image:url(${cpn:unmappedUrl(slingRequest,'/libs/composum/nodes/console/browser/images/image-background.png')})">
            <img class="${widgetCssBase}_picture" src="${cpn:path(widget.model.path)}"/>
        </div>
    </div>
    <div class="${widgetCssBase}_data"></div>
</div>

