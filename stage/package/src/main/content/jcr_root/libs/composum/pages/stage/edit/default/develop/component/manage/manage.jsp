<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog var="model" type="com.composum.pages.commons.model.Component"
                title="Manage Component Elements" selector="generic" languageContext="false"
                submit="/bin/cpm/pages/develop.adjustComponent.json@{model.path}" successEvent="content:changed">
    <sling:call script="embedded.jsp"/>
</cpp:editDialog>
