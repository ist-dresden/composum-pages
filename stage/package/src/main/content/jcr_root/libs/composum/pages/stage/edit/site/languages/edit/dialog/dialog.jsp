<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog var="language" type="com.composum.pages.commons.model.properties.Languages"
                title="Languages" languageContext="false">
    <sling:call script="languages.jsp"/>
</cpp:editDialog>
