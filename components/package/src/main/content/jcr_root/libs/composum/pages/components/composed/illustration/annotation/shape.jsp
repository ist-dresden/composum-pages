<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<cpp:defineObjects/>
<cpp:element var="annotation" type="com.composum.pages.components.model.composed.illustration.Annotation"
             cssSet="@{annotationCssBase}_shape type-@{annotation.shapeType} icon-@{annotation.iconType} btn btn-@{annotation.shapeLevel}"
             style="@{annotation.shapeStyle}" data-id="@{annotationId}">
    <a class="${annotation.iconClasses} ${annotationCssBase}_link" role="button" tabindex="0" data-toggle="popover"
       data-placement="auto ${annotation.placement}" data-content='${cpn:attr(slingRequest,annotation.text,1)}'
       title="${cpn:text(annotation.title)}">${cpn:text(annotation.shapeText)}</a>
</cpp:element>
