<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialogGroup tabId="google" label="Google API" expanded="true">
    <div class="row">
        <div class="col col-xs-8">
            <cpp:widget label="API Key" property="settings/google/api/key" type="textfield"/>
        </div>
    </div>
</cpp:editDialogGroup>
