<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<div class="row">
    <div class="col col-xs-10">
        <cpp:widget label="Subtitle" property="title" type="textfield" i18n="true"/>
    </div>
    <div class="col col-xs-2">
        <cpp:widget label="Hide Title" property="hideTitle" type="checkbox"/>
    </div>
</div>
