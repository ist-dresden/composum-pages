<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<div class="row">
    <div class="col col-xs-12">
        <cpp:widget type="static"
                    value="if the year is less than 10 or blank a value relative to the current year is assumed; the month can be a value between -11 and 12, values less than 1 or a blank value are used to calculate the mont relative to the current month"/>
    </div>
</div>
