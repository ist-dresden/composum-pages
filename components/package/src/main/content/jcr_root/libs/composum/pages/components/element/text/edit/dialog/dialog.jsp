<%@page session="false" pageEncoding="utf-8" %><%--
--%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><cpp:defineFrameObjects/>
<cpp:editDialog var="text" type="com.composum.pages.components.model.text.Text"
                title="@{dialog.selector=='create'?'Create a Text':'Edit Text'}">
    <cpp:widget label="Subtitle" property="title" type="text" i18n="true"/>
    <cpp:widget label="Text" property="text" type="richtext" i18n="true"/>
</cpp:editDialog>
