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
    <cpp:widget label="columns" property="columns" type="select" required="true"
                options="-12-:100%,-6--6-:50% / 50%,-4--8-:33% / 66%,-8--4-:66% / 33%,-4--4--4-:33% / 33% / 33%,-3--6--3-:25% / 50% / 25%,-9--3-:75% / 25%,-3--9-:25% / 75%"/>
</cpp:editDialog>
