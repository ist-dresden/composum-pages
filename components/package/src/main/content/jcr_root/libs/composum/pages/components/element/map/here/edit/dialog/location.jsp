<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialogGroup label="Location" expanded="true">
    <div class="row">
        <div class="col col-xs-3">
        </div>
        <div class="col col-xs-6">
            <cpp:widget label="Street" property="street" type="textfield"/>
        </div>
        <div class="col col-xs-3">
            <cpp:widget label="No" property="number" type="textfield"/>
        </div>
    </div>
    <div class="row">
        <div class="col col-xs-3">
            <cpp:widget label="Zip Code" property="zip" type="textfield"/>
        </div>
        <div class="col col-xs-6">
            <cpp:widget label="City" property="city" type="textfield"/>
        </div>
    </div>
    <div class="row">
        <div class="col col-xs-3">
        </div>
        <div class="col col-xs-6">
            <cpp:widget label="Country" property="country" type="textfield"/>
        </div>
    </div>
</cpp:editDialogGroup>
