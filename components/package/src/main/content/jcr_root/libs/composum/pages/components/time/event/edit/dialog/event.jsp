<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<div class="row">
    <div class="col col-xs-6">
        <cpp:widget label="Date" property="date" type="datetimefield" i18n="false" required="true"
                    hint="the (start) date of the event"/>
    </div>
    <div class="col col-xs-6">
        <cpp:widget label="to" property="dateEnd" type="datetimefield" i18n="false"
                    hint="the optional end date / time"/>
    </div>
</div>
<cpp:widget label="Location" property="map/location" type="textfield" i18n="false" required="true"
            hint="the events location (address)"/>
