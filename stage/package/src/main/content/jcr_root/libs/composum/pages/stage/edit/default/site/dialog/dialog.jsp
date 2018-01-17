<%@page session="false" pageEncoding="utf-8" %><%--
--%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><cpp:defineFrameObjects/>
<cpp:editDialog var="site" type="com.composum.pages.commons.model.Site" selector="generic" languageContext="false"
                title="Site Properties">
    <cpp:widget label="Publish Policy" name="publicMode" type="select" options=",PUBLIC,PREVIEW,LIVE"/>
    <sling:call script="/libs/composum/pages/stage/edit/site/languages/edit/dialog/languages.jsp"/>
</cpp:editDialog>
