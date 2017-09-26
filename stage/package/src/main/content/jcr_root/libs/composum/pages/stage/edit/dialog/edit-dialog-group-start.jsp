<%@page session="false" pageEncoding="utf-8" %><%--
--%><%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><cpp:defineFrameObjects/>
<div class="panel panel-default">
    <div class="panel-heading" role="tab">
        <h4 class="panel-title">
            <a data-toggle="collapse" href="#${dialogGroup.groupId}" aria-expanded="${dialogGroup.expanded}"
               aria-controls="${dialogGroup.groupId}">${dialogGroup.label}</a>
        </h4>
    </div>
    <div id="${dialogGroup.groupId}" class="panel-body panel-collapse collapse ${dialogGroup.expanded?'in':''}" role="tabpanel">
