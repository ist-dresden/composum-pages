<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<cpp:element var="overlay" type="com.composum.pages.components.model.composed.overlay.Overlay">
    <cpn:div test="${!overlay.hideContent||overlay.editMode}" class="${overlayCSS}_background">
        <cpp:include path="background" resourceType="composum/pages/components/composed/overlay/background"/>
    </cpn:div>
    <cpp:include path="foreground" resourceType="composum/pages/components/composed/overlay/foreground"/>
</cpp:element>
