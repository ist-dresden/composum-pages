<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:element var="frame" type="com.composum.pages.stage.model.edit.FramePage" mode="none">
    <div class="${frameCssBase}_page-view">
        <sling:include resourceType="composum/pages/stage/edit/actions/view"/>
    </div>
    <div class="${frameCssBase}_component">
        <sling:include resourceType="composum/pages/stage/edit/actions/initial" replaceSelectors="${frame.displayModeSelector}"/>
    </div>
</cpp:element>
