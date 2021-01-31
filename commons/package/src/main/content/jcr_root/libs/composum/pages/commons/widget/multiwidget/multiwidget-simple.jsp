<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<div class="composum-pages-edit-multiwidget multiwidget-simple widget multi-form-widget form-group"
     data-name="${multiwidget.propertyName}">
    <label class="control-label composum-pages-edit-multiwidget_label"><span class="label-text">${cpn:text(widget.label)}</span><sling:call script="hint.jsp"/></label>
    <div class="multi-form-content">
        <div class="multi-form-item">
