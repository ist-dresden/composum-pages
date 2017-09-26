<%@page session="false" pageEncoding="utf-8" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><cpp:defineFrameObjects/>
<cpp:widget label="Navigation Title" property="navigation/title" type="text" i18n="true"/>
<cpp:widget label="Hide in Navigation" property="navigation/hideInNav" type="checkbox" i18n="false"/>
<cpp:widget label="Ignore in Search" property="search/ignoreInSearch" type="checkbox" i18n="false"/>
