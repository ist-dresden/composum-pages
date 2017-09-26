<%@page session="false" pageEncoding="UTF-8" %><%--
--%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %><%--
--%><%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><cpp:defineFrameObjects/>
<cpp:element var="sites" type="com.composum.pages.stage.model.home.Sites" mode="none">
    <h2 class="${sitesCssBase}_title">Your Sites</h2>
    <div class="${sitesCssBase}_no-site">
        <p class="${sitesCssBase}_paragraph alert alert-warning">
            There is no site available for you on this platform.
        </p>
        <cpn:link href="" classes="${sitesCssBase}_link alert alert-info">Create your first site now...</cpn:link>
    </div>
    <sling:include resourceType="composum/pages/stage/home/sites/tools"/>
</cpp:element>
