<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<cpp:model var="model" type="com.composum.pages.components.model.element.IFrame">
    <iframe class="${modelCSS}_frame" src="${cpn:url(slingRequest,model.src)}" width="100%"
            data-mode="${model.mode}" data-height="${cpn:text(model.height)}"></iframe>
</cpp:model>
