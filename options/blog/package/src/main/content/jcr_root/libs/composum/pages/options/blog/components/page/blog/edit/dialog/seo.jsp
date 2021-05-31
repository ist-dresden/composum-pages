<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialogTab tabId="seo" label="Search Engines">
    <cpp:widget label="Description / Keywords" property="seo/description" type="textarea" height="120"
                i18n="true" hint="a short description for search engines"/>
    <cpp:widget label="Alternatives" name="alternatives" type="textfield" multi="true"
                hint="the set of (historical) URLs/URIs which are replaced by this page; a 'redirect' is triggered for these URLs/URIs if a request would be answered with '404' (not found)"/>
</cpp:editDialogTab>
