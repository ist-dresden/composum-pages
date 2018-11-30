<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog var="row" type="com.composum.pages.components.model.composed.table.Row"
                title="@{dialog.selector=='create'?'Create a Table Row':'Table Row Properties'}">
    <div class="row">
        <div class="col col-xs-3">
        </div>
        <div class="col col-xs-3">
            <cpp:widget label="Table Head" name="head" type="checkbox"/>
        </div>
        <div class="col col-xs-6">
            <cpp:widget label="Warning Level" property="level" type="select"
                        hint="<a href='https://getbootstrap.com/docs/3.3/css/#tables-contextual-classes' target='_blank'>'Bootstrap' background</a>"
                        options=",active,info,success,warning,danger"/>
        </div>
    </div>
</cpp:editDialog>
