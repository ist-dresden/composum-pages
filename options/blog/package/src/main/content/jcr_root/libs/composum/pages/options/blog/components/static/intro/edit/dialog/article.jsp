<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<sling:call script="header.jsp"/>
<div class="row">
    <div class="col col-xs-7">
        <cpp:widget label="Author" property="meta/author" type="textfield" required="true"/>
    </div>
    <div class="col col-xs-5">
        <cpp:widget label="Date" property="meta/date" type="datefield" required="true"/>
    </div>
</div>
