<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialogGroup label="Image" expanded="true">
    <div class="row">
        <div class="col col-xs-3">
            <cpp:widget label="Width" property="width" type="textfield"/>
        </div>
        <div class="col col-xs-3">
            <cpp:widget label="Height" property="height" type="textfield"/>
        </div>
        <div class="col col-xs-4">
            <cpp:widget label="Type" property="type" type="select"
                        options="0:map view,1:satellite view,2:terrain map,3:hybrid map"/>
        </div>
        <div class="col col-xs-2">
            <cpp:widget label="Zoom" property="zoom" type="textfield"/>
        </div>
    </div>
</cpp:editDialogGroup>
