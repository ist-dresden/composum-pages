<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog title="@{dialog.selector=='create'?'Create an iFrame':'Edit iFrame'}">
    <cpp:widget label="Title" property="title" type="textfield" i18n="true"
                hint="the optional title shown as the frames header"/>
    <cpp:widget label="Frame Source" property="src" type="pathfield"
                hint="the 'src' of the iframe - the content URL of the path in the repository"/>
    <cpp:widget label="Service URI" property="serviceUri" type="textfield"
                hint="an optional URI of a servlet prepended to the 'src'"/>
    <cpp:widget label="Copyright" property="copyright" type="textfield" i18n="true"
                hint="copyright notice if necessary or useful"/>
    <div class="row">
        <div class="col col-xs-3">
            <cpp:widget label="Style" property="style" type="select"
                        options="simple,bordered,panel" default="simple"/>
        </div>
        <div class="col col-xs-3">
            <cpp:widget label="Auto Height" property="mode" type="select" hint="calc rule"
                        options="body,elements" default="body"/>
        </div>
        <div class="col col-xs-3">
            <cpp:widget label="Collapsed Height" property="height" type="textfield"/>
        </div>
        <div class="col col-xs-3">
            <cpp:widget label="Expandable" property="expandable" type="checkbox"/>
        </div>
    </div>
</cpp:editDialog>
