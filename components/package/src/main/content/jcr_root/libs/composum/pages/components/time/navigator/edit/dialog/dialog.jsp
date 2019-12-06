<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog title="Navigator Settings">
    <div class="row">
        <div class="col col-xs-9">
            <div class="row">
                <div class="col col-xs-4">
                    <cpp:widget label="Year" property="year" type="textfield"/>
                </div>
                <div class="col col-xs-4">
                    <cpp:widget label="Month" property="month" type="numberfield" options="-11;12;1;0" blank="true"/>
                </div>
                <div class="col col-xs-4">
                    <cpp:widget label="Navigation" property="showNavigation" type="checkbox"
                                hint="enable navigation"/>
                </div>
            </div>
        </div>
        <div class="col col-xs-3">
            <cpp:widget label="Content Type" property="itemType" type="select" options=":all,event,news"/>
        </div>
    </div>
    <div class="row" style="align-items: flex-start;">
        <div class="col col-xs-9">
            <sling:call script="range-hint.jsp"/>
            <div class="row">
                <div class="col col-xs-8">
                    <cpp:widget label="Search Term" property="term" type="textfield"
                                hint="a text pattern for filtering"/>
                </div>
                <div class="col col-xs-4">
                    <cpp:widget label="editable term" property="inputTerm" type="checkbox"
                                hint="enable term editing"/>
                </div>
            </div>
            <div class="row">
                <div class="col col-xs-12">
                    <cpp:widget label="Search Root" property="searchRoot" type="pathfield"
                                hint="the root path to determine the items (default: the site root)"/>
                </div>
            </div>
        </div>
        <div class="col col-xs-3">
            <cpp:widget label="Category" property="category" type="textfield" multi="true" hint="for filtering"/>
        </div>
    </div>
</cpp:editDialog>
