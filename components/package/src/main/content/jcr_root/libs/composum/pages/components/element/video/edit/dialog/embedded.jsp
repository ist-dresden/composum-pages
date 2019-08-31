<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<div class="row">
    <div class="col col-xs-6">
        <cpp:widget label="Video" property="videoRef" type="videofield"
                    hint="the path to the video asset in the repository or an URL"/>
    </div>
    <div class="col col-xs-6">
        <cpp:widget label="Poster" property="posterRef" type="imagefield"
                    hint="the path to the preview image in the repository or an URL"/>
    </div>
</div>
<div class="row">
    <div class="col col-xs-3">
        <cpp:widget label="Controls" property="controls" type="checkbox"/>
    </div>
    <div class="col col-xs-3">
        <cpp:widget label="Autoplay" property="autoplay" type="checkbox"/>
    </div>
    <div class="col col-xs-3">
        <cpp:widget label="Muted" property="muted" type="checkbox"/>
    </div>
    <div class="col col-xs-3">
        <cpp:widget label="Loop" property="loop" type="checkbox"/>
    </div>
</div>
<sling:call script="meta.jsp"/>
