<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog title="@{dialog.selector=='create'?'Create a Row':'Row Properties'}">
    <div class="row">
        <div class="col col-xs-9">
            <cpp:widget label="Title" property="title" type="textfield" i18n="true"
                        hint="an optional title as row header"/>
        </div>
        <div class="col col-xs-3">
            <cpp:widget label="Anchor" property="anchor" type="textfield"
                        hint="optional navigation id"/>
        </div>
    </div>
    <sling:call script="columns.jsp"/>
</cpp:editDialog>
