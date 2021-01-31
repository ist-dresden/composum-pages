<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog var="model" type="com.composum.pages.commons.model.Folder" selector="generic" languageContext="false"
                title="Activate Content" submitLabel="Activate" data-path="@{model.path}">
    <%-- the selected pages in the requests 'pages' attribute - not rendered if no such attribute declared --%>
    <cpp:widget type="hidden" name="target" request="pages"/>
    <cpp:widget label="Page References" type="page-references" name="page-references" scope="page" unresolved="true"
                hint="select unresolved page references for activation"/>
    <cpp:widget label="Asset References" type="page-references" name="asset-references" scope="asset" unresolved="true"
                hint="select unresolved assets references for activation"/>
    <cpp:widget type="static"
                value="The elements listed above are referenced directly or indirectly by this page and not part of the designated release (probably modified but not activated yet). All selected references will be activated together with this page."
                i18n="true"/>
</cpp:editDialog>
