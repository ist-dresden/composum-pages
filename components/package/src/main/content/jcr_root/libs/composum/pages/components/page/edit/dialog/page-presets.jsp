<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialogTab tabId="styles" label="Page Styles">
    <div class="row">
        <div class="col col-xs-5">
        </div>
        <div class="col col-xs-7">
            <sling:call script="page-style.jsp"/>
        </div>
    </div>
</cpp:editDialogTab>
