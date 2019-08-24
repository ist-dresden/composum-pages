<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:widget label="Video" property="videoRef" type="videofield"
            hint="the path to the video asset in the repository or an URL"/>
<div class="row">
    <div class="col col-xs-3">
        <cpp:widget label="Controls" type="checkbox" disabled="${true}"/>
    </div>
    <div class="col col-xs-3">
        <cpp:widget label="Autoplay" type="checkbox" disabled="true" value="${true}"/>
    </div>
    <div class="col col-xs-3">
        <cpp:widget label="Muted" type="checkbox" disabled="true" value="${true}"/>
    </div>
    <div class="col col-xs-3">
        <cpp:widget label="Loop" property="loop" type="checkbox"/>
    </div>
</div>
<sling:call script="meta.jsp"/>
