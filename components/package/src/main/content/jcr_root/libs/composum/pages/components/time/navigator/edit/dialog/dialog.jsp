<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog title="Event Settings">
    <div class="row" style="align-items: flex-start;">
        <div class="col col-xs-9">
            <div class="row">
                <div class="col col-xs-4">
                    <cpp:widget label="Year" property="year" type="textfield"
                                hint="blank or <10: relative to the current year"/>
                </div>
                <div class="col col-xs-4">
                    <cpp:widget label="Month" property="month" type="numberfield" options="-11;12;1;0" blank="true"
                                hint="'0' or blank: current month"/>
                </div>
                <div class="col col-xs-4">
                    <cpp:widget label="Navigation" property="showNavigation" type="checkbox"
                                hint="enable navigation"/>
                </div>
            </div>
            <div class="row">
                <div class="col col-xs-8">
                    <cpp:widget label="Search Term" property="term" type="textfield"/>
                </div>
                <div class="col col-xs-4">
                    <cpp:widget label="editable term" property="inputTerm" type="checkbox"
                                hint="enable term editing"/>
                </div>
            </div>
        </div>
        <div class="col col-xs-3">
            <cpp:widget label="Category" property="category" type="textfield" multi="true"
                        hint="for filtering by category"/>
        </div>
    </div>
</cpp:editDialog>
