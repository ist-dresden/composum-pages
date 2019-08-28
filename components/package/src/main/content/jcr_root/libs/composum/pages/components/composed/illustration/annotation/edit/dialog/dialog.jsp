<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog title="@{dialog.selector=='create'?'Create an Annotation':'Edit Annotation'}">
    <div class="row">
        <div class="col col-xs-8">
            <sling:call script="content.jsp"/>
        </div>
        <div class="col col-xs-4">
            <sling:call script="shape.jsp"/>
        </div>
    </div>
</cpp:editDialog>
