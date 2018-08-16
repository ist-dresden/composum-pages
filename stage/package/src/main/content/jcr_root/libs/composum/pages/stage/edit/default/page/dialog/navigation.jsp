<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<div class="row">
    <div class="col col-xs-4">
        <cpp:widget label="Navigation Title" property="navigation/title" type="textfield" i18n="true"/>
    </div>
    <div class="col col-xs-4">
        <cpp:widget label="Hide in Navigation" property="navigation/hideInNav" type="checkbox" i18n="false"/>
    </div>
    <div class="col col-xs-4">
        <cpp:widget label="Ignore in Search" property="search/ignoreInSearch" type="checkbox" i18n="false"/>
    </div>
</div>
