<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog var="container" type="com.composum.pages.commons.model.Container"
                title="${cpn:i18n(slingRequest,'Add a new Element')}" selector="new">
    <cpp:widget label="${cpn:i18n(slingRequest,'Select the element type')}" name="elementType" type="element-type-select"
                options="${container.elementTypes}"/>
</cpp:editDialog>
