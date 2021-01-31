<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog var="model" type="com.composum.pages.commons.model.Page" selector="generic" languageContext="false"
                title="Deactivate Content" submitLabel="Deactivate" data-path="@{model.path}">
    <cpp:widget type="hidden" name="target" request="pages"/>
    <cpp:widget type="static" level="warning"
                value="This element will be removed from the current release. If this release is live the element is removed from the public site."
                i18n="true"/>
    <cpp:widget label="Content Referrers" type="page-referrers" name="page-referrers" scope="page" resolved="true"
                hint="select active content referrers for deactivation"/>
    <cpp:widget type="static"
                value="The elements listed above are referencing this element and are part of the designated release. All selected referrers will be deactivated also together with this element."
                i18n="true"/>
</cpp:editDialog>
