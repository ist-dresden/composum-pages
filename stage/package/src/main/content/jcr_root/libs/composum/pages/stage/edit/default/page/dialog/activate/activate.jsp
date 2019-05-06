<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog var="model" type="com.composum.pages.commons.model.Page" selector="generic" languageContext="false"
                title="Activate Page" submitLabel="Activate">
    <cpp:widget label="Page References" type="page-references" name="page-references" scope="page" unresolved="true"
                hint="select unresolved page references for activation"/>
    <cpp:widget label="Asset References" type="page-references" name="asset-references" scope="asset" unresolved="true"
                hint="select unresolved assets references for activation"/>
    <cpp:widget type="static"
                value="The elements listed above are referenced by this page and not part of the designated release (probaly modified but not activated yet). All selected references will be activated together with this page."
                i18n="true"/>
</cpp:editDialog>