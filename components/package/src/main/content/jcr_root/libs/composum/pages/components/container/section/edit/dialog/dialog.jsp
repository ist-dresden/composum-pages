<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog title="@{dialog.selector=='create'?'Create a Row':'Row Properties'}">
    <div class="row">
        <div class="col col-xs-7">
            <cpp:widget label="Title" property="jcr:title" type="textfield" i18n="true"
                        hint="an optional title as section header"/>
        </div>
        <div class="col col-xs-3">
            <cpp:widget label="Anchor" property="anchor" type="textfield"
                        hint="optional navigation id"/>
        </div>
        <div class="col col-xs-2">
            <cpp:widget label="Hide Title" property="hideTitle" type="checkbox"/>
        </div>
    </div>
</cpp:editDialog>
