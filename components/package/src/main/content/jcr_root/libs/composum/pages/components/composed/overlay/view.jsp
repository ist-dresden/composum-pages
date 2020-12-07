<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<cpp:defineObjects/>
<cpp:model var="overlay" type="com.composum.pages.components.model.composed.overlay.Overlay">
    <cpn:div test="${!overlay.hideContent||overlay.editMode}" class="${overlayCSS}_background">
        <cpp:include path="background" resourceType="composum/pages/components/composed/overlay/background"/>
    </cpn:div>
    <cpp:include path="foreground" resourceType="composum/pages/components/composed/overlay/foreground"/>
</cpp:model>
