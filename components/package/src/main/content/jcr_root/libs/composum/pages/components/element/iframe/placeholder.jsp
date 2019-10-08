<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<cpp:model var="model" type="com.composum.pages.components.model.element.IFrame">
    <cpp:dropZone property="src" filter="page">
        <div class="composum-pages-components-placeholder">
            <span class="fa-stack composum-pages-components-placeholder_icon"><i
                    class="fa fa-object-group fa-stack-2x"></i></span>
            <span class="value">${cpn:url(slingRequest,model.src)}</span>
        </div>
    </cpp:dropZone>
</cpp:model>