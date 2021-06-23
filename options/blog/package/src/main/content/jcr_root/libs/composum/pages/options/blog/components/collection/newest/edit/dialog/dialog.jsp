<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpn:bundle basename="composum-pages-option-blog"/>
<cpp:editDialog title="Newest Blog Article Filter">
    <div class="row">
        <div class="col col-xs-2">
            <cpp:widget label="max" property="maxCount" type="numberfield" default="10"/>
        </div>
        <div class="col col-xs-10">
            <cpp:widget label="Search Root" property="searchRoot" type="pathfield"
            hint="the repository root path for the search; default: the blog root page"/>
        </div>
    </div>
    <div class="row">
        <div class="col col-xs-12">
            <cpp:widget label="Text" property="pattern" type="textfield"
            hint="a search text pattern to filter the articles by their content"/>
        </div>
    </div>
    <div class="row">
        <div class="col col-xs-7">
            <cpp:widget label="Author" property="author" type="textfield"/>
        </div>
        <div class="col col-xs-5">
            <div class="row">
                <div class="col col-xs-5">
                    <cpp:widget label="Date" property="period" type="select"
                                options="lastMonth:last month,lastYear:last year,about,before,after"/>
                </div>
                <div class="col col-xs-7">
                    <cpp:widget label="..." property="date" type="datefield"/>
                </div>
            </div>
        </div>
    </div>
</cpp:editDialog>
