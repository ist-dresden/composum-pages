<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog title="Sites Sitemap" successEvent="page:reload">
    <cpp:widget label="Sitemap Root" property="sitemapRootPath" type="pathfield"
                hint="the root path if a special root should be used; default: the site itself"/>
    <cpp:widget label="Robot Rules" property="robotsTxt" type="textarea" rows="10"
                hint="the content of the sites 'robots.txt' (if forwarded to /sitemap.robots.txt)"/>
    <sling:call script="navigation.jsp"/>
</cpp:editDialog>
