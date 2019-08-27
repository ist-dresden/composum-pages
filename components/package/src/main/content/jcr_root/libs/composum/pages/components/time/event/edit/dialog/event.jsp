<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<div class="row">
    <div class="col col-xs-7">
        <cpp:widget label="Date" property="date" type="datetimefield" i18n="false" required="true"
                    hint="the (start) date of the event"/>
    </div>
    <div class="col col-xs-5">
        <cpp:widget label="to" property="dateEnd" type="datetimefield" i18n="false"
                    hint="the optional end date / time"/>
    </div>
</div>
<div class="row">
    <div class="col col-xs-7">
        <cpp:widget label="Location" property="map/location" type="textfield" i18n="false" required="true"
                    hint="the events location (address)"/>
        <cpp:include resourceType="composum/pages/components/element/link" subtype="edit/dialog"
                     replaceSelectors="embedded"/>
    </div>
    <div class="col col-xs-5">
        <cpp:widget label="Logo Image" property="logo/imageRef" type="imagefield"/>
    </div>
</div>
