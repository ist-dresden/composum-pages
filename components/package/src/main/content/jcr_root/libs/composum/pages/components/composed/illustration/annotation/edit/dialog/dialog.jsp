<%@page session="false" pageEncoding="utf-8" %><%--
--%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><cpp:defineFrameObjects/>
<cpp:editDialog var="annotation" type="com.composum.pages.components.model.text.Text"
                title="@{dialog.selector=='create'?'Create an Annotation':'Edit Annotation'}">
    <cpp:editDialogTab tabId="content" label="Content">
        <sling:call script="content.jsp"/>
    </cpp:editDialogTab>
    <cpp:editDialogTab tabId="shape" label="Shape">
        <sling:call script="shape.jsp"/>
    </cpp:editDialogTab>
</cpp:editDialog>
