<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog var="table" type="com.composum.pages.components.model.composed.table.Table"
                title="@{dialog.selector=='create'?'Create a Table':'Edit Table'}">
    <div class="row">
        <div class="col col-xs-12">
            <cpp:widget label="Subtitle" property="title" type="textfield" i18n="true"
                        hint="an optional title above the table"/>
        </div>
    </div>
    <div class="row">
        <div class="col col-xs-12">
            <cpp:widget label="Copyright" property="copyright" type="textfield" i18n="true"
                        hint="copyright notice or another annotation below the table"/>
        </div>
    </div>
    <div class="row" style="display:inline-block;margin-top:-25px">
        <div class="col col-xs-3">
            <cpp:widget label="striped" name="striped" type="checkbox"/>
        </div>
        <div class="col col-xs-3">
            <cpp:widget label="bordered" name="bordered" type="checkbox"/>
        </div>
        <div class="col col-xs-3">
            <cpp:widget label="condensed" name="condensed" type="checkbox"/>
        </div>
        <div class="col col-xs-3">
            <cpp:widget label="hover" name="hover" type="checkbox"/>
        </div>
    </div>
</cpp:editDialog>
