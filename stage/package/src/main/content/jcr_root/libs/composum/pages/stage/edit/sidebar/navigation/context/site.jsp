<%@page session="false" pageEncoding="UTF-8"%>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:model var="site" type="com.composum.pages.stage.model.edit.site.SiteElement" mode="none">
    <sling:call script="actions.jsp"/>
    <cpp:include resource="${site.site.resource}" subtype="edit/tile" replaceSelectors="status"/>
</cpp:model>
