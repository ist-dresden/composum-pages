<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<div class="row">
    <div class="col col-xs-7">
        <cpp:widget label="Date" property="date" type="datetimefield" required="true"
                    hint="the date of the news publication"/>
    </div>
</div>
<div class="row">
    <div class="col col-xs-12">
        <cpp:widget label="Top Background Image" property="image/imageRef" type="imagefield"/>
    </div>
</div>