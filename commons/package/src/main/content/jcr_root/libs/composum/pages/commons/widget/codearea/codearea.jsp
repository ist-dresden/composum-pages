<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<cpp:defineFrameObjects/>
<div class="${widgetCSS}_${widget.widgetType} ${widgetCSS}_${widget.cssName}${widget.required?' required':''} form-group">
    <sling:call script="label.jsp"/>
    <div class="${widgetCSS}_editor codearea-widget widget code-editor form-control widget-name_${widget.cssName}"
         data-name="${widget.name}" data-height="${widget.model.height}" data-label="${widget.label}"
         data-i18n="${widget.i18n}" ${widget.attributes} data-encoded="${widget.model.encoded}"></div>
</div>
