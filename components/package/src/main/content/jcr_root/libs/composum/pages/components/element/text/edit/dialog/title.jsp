<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<div class="row">
    <div class="col col-xs-9">
        <cpp:widget label="Subtitle" property="title" type="textfield" i18n="true"/>
    </div>
    <div class="col col-xs-3">
        <cpp:widget label="Title Level" property="titleLevel" type="numberfield" options="1:6"/>
    </div>
</div>
