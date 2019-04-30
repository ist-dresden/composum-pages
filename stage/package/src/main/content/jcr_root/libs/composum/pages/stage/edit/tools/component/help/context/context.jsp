<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:element var="element" type="com.composum.pages.stage.model.edit.FrameElement" mode="none"
             cssBase="composum-pages-tools"
             cssAdd="@{elementCssBase}_help-context pages-mode_@{element.displayMode}">
    <cpn:div test="${element.developMode}" class="composum-pages-tools_actions btn-toolbar">
        <div class="${elementCssBase}_left-actions">
        </div>
        <div class="${elementCssBase}_right-actions">
            <div class="${elementCssBase}_button-group btn-group btn-group-sm" role="group">
            </div>
            <div class="${elementCssBase}_button-group btn-group btn-group-sm" role="group">
            </div>
        </div>
    </cpn:div>
    <div class="${elementCssBase}_panel">
        <cpp:include
                resourceType="composum/pages/stage/edit/tools/component/help/${element.developMode?'edit':'view'}"/>
    </div>
</cpp:element>
