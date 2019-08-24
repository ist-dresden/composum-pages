<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog var="element" type="com.composum.pages.commons.model.Element" selector="delete"
                title="Delete Element" languageContext="false">
    ${cpn:i18n(slingRequest,'Do you really want to delete the selected element?')}
</cpp:editDialog>
