<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:model var="element" type="com.composum.pages.stage.model.edit.FrameElement"
           cssBase="composum-pages-tools">
    <div class="${elementCssBase}_help-view">
        <cpp:include test="${not empty element.component.helpContent}" path="${element.component.helpContent}"/>
    </div>
</cpp:model>
