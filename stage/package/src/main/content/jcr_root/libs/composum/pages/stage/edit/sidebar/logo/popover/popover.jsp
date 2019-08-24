<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:model var="frame" type="com.composum.pages.stage.model.edit.FrameModel" mode="none">
    <div class="${frameCssBase}_user">
        <cpn:text class="${frameCssBase}_username">${frame.userId}</cpn:text>
        <cpn:link class="${frameCssBase}_logout"
                  href="/system/sling/logout.html?logout=true&GLO=true&resource=/">${cpn:i18n(slingRequest, 'Logout')}</cpn:link>
    </div>
    <div class="${frameCssBase}_actions">
        <cpn:link href="?pages.mode=${frame.developMode?'edit':'develop'}" test="${frame.developModeAllowed}"
                  class="${frameCssBase}_develop-mode fa fa-wrench btn btn-sm btn-default${frame.developMode?' active':''}"
                  title="${cpn:i18n(slingRequest,'Toggle Develop Mode')}"></cpn:link>
    </div>
    <div class="${frameCssBase}_consoles">
        <sling:include path="/libs/composum/nodes/console/content" replaceSelectors="consoles"/>
    </div>
</cpp:model>
