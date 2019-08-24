<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<cpp:defineFrameObjects/>
<div class="${widgetCSS}_preview">
    <div class="${widgetCSS}_wrapper">
        <div class="${widgetCSS}_frame">
            <img class="${widgetCSS}_picture" src="${cpn:url(slingRequest,widget.model.path)}"
                 style="background-image:url(${cpn:unmappedUrl(slingRequest,'/libs/composum/nodes/commons/images/image-background.png')})"/>
        </div>
    </div>
    <div class="${widgetCSS}_data"></div>
</div>

