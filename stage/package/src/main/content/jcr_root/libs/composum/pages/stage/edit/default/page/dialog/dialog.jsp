<%@page session="false" pageEncoding="utf-8" %><%--
--%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %><%--
--%><%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><cpp:defineFrameObjects/>
<cpp:editDialog var="container" type="com.composum.pages.commons.model.Container"
                title="Edit Page Properties" selector="generic">
    <cpp:widget label="Page Title" property="jcr:title" type="text" i18n="true"/>
    <cpp:widget label="Navigation Title" property="navigation/title" type="text" i18n="true"/>
    <cpp:widget label="Hide in Navigation" property="navigation/hideInNav" type="checkbox" i18n="false"/>
    <cpp:widget label="Ignore in Search" property="search/ignoreInSearch" type="checkbox" i18n="false"/>
</cpp:editDialog>
