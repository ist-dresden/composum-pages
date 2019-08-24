<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<div ${multiwidget.attributes}>
    <input type="hidden" name="${multiwidget.propertyName}/@Delete"/>
    <label class="control-label ${multiwidgetCSS}_label">${multiwidget.label}</label>
    <div class="multi-form-content">
        <div class="multi-form-item">
