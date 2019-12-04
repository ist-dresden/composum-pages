<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog title="Calendar Settings">
    <div class="row">
        <div class="col col-xs-3">
            <cpp:widget label="Year" property="year" type="textfield"
                        hint="blank or <10: relative to the current year"/>
        </div>
        <div class="col col-xs-3">
            <cpp:widget label="Month" property="month" type="numberfield" options="-11;12;1;0" blank="true"
                        hint="'0' or blank: current month"/>
        </div>
        <div class="col col-xs-3">
            <cpp:widget label="Columns" property="columns" type="numberfield" options="1;6" blank="true"/>
        </div>
        <div class="col col-xs-3">
            <cpp:widget label="Rows" property="rows" type="numberfield" options="1;6" blank="true"/>
        </div>
    </div>
    <cpp:widget label="Detail Page" property="detailPage" type="pathfield"
                hint="the path of the page to open if a day is choosen - if not on the same page"/>
    <div class="row">
        <div class="col col-xs-4">
            <cpp:widget label="Navigation" property="showNavigation" type="checkbox"
                        hint="enable calendar navigation"/>
        </div>
        <div class="col col-xs-4">
            <cpp:widget label="Weekdays" property="showWeekdayLabels" type="checkbox"
                        hint="show short weekday labels"/>
        </div>
        <div class="col col-xs-4">
            <cpp:widget label="Week Numbers" property="showWeekNumbers" type="checkbox"
                        hint="number of week in the year"/>
        </div>
    </div>
</cpp:editDialog>
