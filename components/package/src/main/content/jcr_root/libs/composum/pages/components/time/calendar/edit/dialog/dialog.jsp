<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog title="Calendar Settings">
    <div class="row" style="align-items: flex-start;">
        <div class="col col-xs-9">
            <div class="row">
                <div class="col col-xs-3">
                    <cpp:widget label="Year" property="year" type="textfield"/>
                </div>
                <div class="col col-xs-3">
                    <cpp:widget label="Month" property="month" type="numberfield" options="-11;12;1;0" blank="true"/>
                </div>
                <div class="col col-xs-3">
                    <cpp:widget label="Columns" property="columns" type="numberfield" options="1;6" blank="true"/>
                </div>
                <div class="col col-xs-3">
                    <cpp:widget label="Rows" property="rows" type="numberfield" options="1;6" blank="true"/>
                </div>
            </div>
            <sling:call script="range-hint.jsp"/>
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
            <div class="row">
                <div class="col col-xs-12">
                    <cpp:widget label="Detail Page" property="detailPage" type="pathfield"
                                hint="the path of the page to open if a day is choosen - if not on the same page"/>
                </div>
            </div>
        </div>
        <div class="col col-xs-3">
            <cpp:widget label="Category" property="category" type="textfield" multi="true"
                        hint="for filtering by category"/>
        </div>
    </div>
    <div class="row">
        <div class="col col-xs-9">
            <cpp:widget label="Search Root" property="searchRoot" type="pathfield"
                        hint="the root path to determine the items (default: the site root)"/>
        </div>
        <div class="col col-xs-3">
            <cpp:widget label="Search Term" property="term" type="textfield"/>
        </div>
    </div>
</cpp:editDialog>
