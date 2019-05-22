<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog var="model" type="com.composum.pages.commons.model.Page" selector="generic" languageContext="false"
                title="Revert Page" submitLabel="Revert">
    <cpp:widget type="static" level="warning"
                value="This page will be reset in the current release. If this release is live the page is restored by the last released version."
                i18n="true"/>
    <cpp:widget label="Page Referrers" type="page-referrers" name="page-referrers" scope="page" resolved="true"
                hint="select active page referrers for revert"/>
    <cpp:widget type="static"
                value="The elements listed above are referencing this page and are part of the designated release. All selected referrers will be reverted also together with this page."
                i18n="true"/>
</cpp:editDialog>
