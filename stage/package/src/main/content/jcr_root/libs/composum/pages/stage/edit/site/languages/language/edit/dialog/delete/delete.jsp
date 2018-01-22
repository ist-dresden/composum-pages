<%@page session="false" pageEncoding="utf-8" %><%--
--%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %><%--
--%><%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><cpp:defineFrameObjects/>
<cpp:editDialog var="language" type="com.composum.pages.stage.model.edit.site.Languages"
                title="Delete Site Language" selector="delete">
    <cpp:widget label="Name" name=":name" value="${language.name}" type="textfield" disabled="true"/>
    <cpp:widget label="Key" property="key" type="textfield" disabled="true"/>
    <cpp:widget label="Label" property="label" type="textfield" disabled="true"/>
</cpp:editDialog>
