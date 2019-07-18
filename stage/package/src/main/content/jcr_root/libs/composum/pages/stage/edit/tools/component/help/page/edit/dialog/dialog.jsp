<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog var="model" type="com.composum.pages.commons.model.Page"
                title="Help Page Properties">
    <cpp:widget label="Title" property="jcr:title" type="textfield" i18n="true"
                hint="the page title / headline"/>
    <cpp:widget label="Description" property="jcr:description" type="richtext" i18n="true"
                hint="a short abstract / teaser text of the page"/>
</cpp:editDialog>
