<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<cpp:defineObjects/>
<cpp:element var="model" type="com.composum.pages.components.model.composed.overlay.Overlay">
    <ul class="${modelCSS}_tabs nav nav-tabs" role="tablist">
        <li role="presentation" class="active"><a
                href="#${model.domId}_background" aria-controls="${model.domId}_background" role="tab"
                data-toggle="tab">Background Content</a></li>
        <li role="presentation"><a
                href="#${model.domId}_foreground" aria-controls="${model.domId}_foreground" role="tab"
                data-toggle="tab">Foreground Overlay</a></li>
    </ul>
    <div class="tab-content">
        <div id="${model.domId}_background" role="tabpanel" class="tab-pane active">
            <div class="${modelCSS}_background">
                <cpp:include path="background" resourceType="composum/pages/components/composed/overlay/background"/>
            </div>
        </div>
        <div id="${model.domId}_foreground" role="tabpanel" class="tab-pane">
            <cpn:div class="${modelCSS}_foreground ${modelCSS}_foreground-${model.hideContent?'replace':'modify'}">
                <cpp:include path="foreground" resourceType="composum/pages/components/composed/overlay/foreground"/>
            </cpn:div>
        </div>
    </div>
</cpp:element>
