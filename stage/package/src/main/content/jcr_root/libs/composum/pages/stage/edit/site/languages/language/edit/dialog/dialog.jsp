<%@page session="false" pageEncoding="utf-8" %><%--
--%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %><%--
--%><%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><cpp:defineFrameObjects/>
<cpp:editDialog var="language" type="com.composum.pages.stage.model.edit.site.Language"
                title="@{language.new?'New':'Edit'} Site Language" selector="@{language.new?'create':''}"
                primaryType="none" resourceType="none">
    <cpp:widget label="Name" name=":name" value="${language.name}" type="text" disabled="${not language.new}"/>
    <cpp:widget label="Key" property="key" type="text"/>
    <cpp:widget label="Label" property="label" type="text"/>
    <cpp:widget label="Dir" property="direction" type="select" options=",ltr,rtl"/>
</cpp:editDialog>
