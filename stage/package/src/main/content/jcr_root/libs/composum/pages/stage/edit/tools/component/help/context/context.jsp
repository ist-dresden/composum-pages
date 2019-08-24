<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:element var="element" type="com.composum.pages.stage.model.edit.FrameElement" mode="none"
             cssBase="composum-pages-tools"
             cssAdd="@{elementCssBase}_help-context pages-mode_@{element.displayMode}">
    <div class="${elementCssBase}_panel">
        <cpp:include replaceSelectors="content"/>
    </div>
</cpp:element>
