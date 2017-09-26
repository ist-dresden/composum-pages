<%@page session="false" pageEncoding="utf-8" %><%--
--%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %><%--
--%><%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><cpp:defineFrameObjects/>
<cpp:editDialog var="item" type="com.composum.pages.commons.model.Element"
                title="@{dialog.selector=='create'?'Create an Accordion':'Accordion Properties'}">
    <cpp:widget type="select" label="Behavior" property="behavior" options="accordion,independent"/>
</cpp:editDialog>
