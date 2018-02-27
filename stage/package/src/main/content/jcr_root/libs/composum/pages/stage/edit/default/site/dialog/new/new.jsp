<%@page session="false" pageEncoding="utf-8" %><%--
--%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %><%--
--%><%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><cpp:defineFrameObjects/>
<cpp:editDialog var="site" type="com.composum.pages.commons.model.Site"
                title="${cpn:i18n(slingRequest,'Add new Content')}" selector="new">
    <cpp:widget label="${cpn:i18n(slingRequest,'Select the content type')}" name="contentType" type="radiolist"
                options="${site.contentTypes}"/>
</cpp:editDialog>
