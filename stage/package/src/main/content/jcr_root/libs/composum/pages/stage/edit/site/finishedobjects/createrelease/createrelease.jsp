<%@page session="false" pageEncoding="utf-8" %><%--
--%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %><%--
--%><%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><cpp:defineFrameObjects/>
<cpp:editDialog tagId="createrelease-dialog" var="site" type="com.composum.pages.commons.model.Site"
                title="Create Release" selector="generic" submitLabel="Create" languageContext="false">
    <input name="objects" type="hidden" value="" class="${siteCssBase}_hidden ${siteCssBase}_objects"/>
    <input name="path" type="hidden" value="${resource.path}" class="${siteCssBase}_hidden ${siteCssBase}_path"/>
    <cpp:widget label="Release Key" name="releaseName" placeholder="enter release key" type="textfield"/>
    <cpp:widget label="Release Title" name="title" placeholder="enter release title" type="textfield"/>
    <cpp:widget label="Description" name="description" placeholder="enter a description" type="textfield"/>
</cpp:editDialog>
