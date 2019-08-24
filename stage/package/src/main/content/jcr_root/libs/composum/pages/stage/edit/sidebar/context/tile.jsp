<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:model var="element" type="com.composum.pages.stage.model.edit.FrameModel">
    <cpp:include path="${element.path}" resourceType="${element.type}"
                 subtype="edit/tile" replaceSelectors="context"/>
</cpp:model>
