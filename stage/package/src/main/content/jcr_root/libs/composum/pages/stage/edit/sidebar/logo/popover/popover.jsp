<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:model var="model" type="com.composum.platform.models.simple.SimpleModel" mode="none">
    <div class="${modelCssBase}_user">
        <cpn:text class="${modelCssBase}_username">${model.username}</cpn:text>
        <cpn:link class="${modelCssBase}_logout"
                  href="/system/sling/logout.html?logout=true&GLO=true&resource=${slingRequest.requestURI}">${cpn:i18n(slingRequest, 'Logout')}</cpn:link>
    </div>
    <div class="${modelCssBase}_consoles">
        <sling:include path="/libs/composum/nodes/console/content" replaceSelectors="consoles"/>
    </div>
</cpp:model>
