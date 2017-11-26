<%@page session="false" pageEncoding="utf-8" %><%--
--%><%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><cpp:defineFrameObjects/>
<cpp:editDialog var="element" type="com.composum.pages.commons.model.Element"
                title="${cpn:i18n(slingRequest,'Create Site')}" selector="create">
    <cpp:include resourceType="composum/pages/stage/edit/site/templates"/>
</cpp:editDialog>
