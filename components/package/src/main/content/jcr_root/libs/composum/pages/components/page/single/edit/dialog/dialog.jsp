<%@page session="false" pageEncoding="utf-8" %><%--
--%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><cpp:defineFrameObjects/>
<cpp:editDialog var="pageProps" type="com.composum.pages.commons.model.Page"
                title="Single Page Properties">
    <sling:call script="general.jsp"/>
    <cpp:widget label="Logo Link" property="logoLink" type="link"/>
    <sling:call script="logo.jsp"/>
    <sling:call script="navigation.jsp"/>
</cpp:editDialog>
