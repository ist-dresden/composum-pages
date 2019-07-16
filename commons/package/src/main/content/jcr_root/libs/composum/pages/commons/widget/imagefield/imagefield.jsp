<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<cpp:defineFrameObjects/>
<div class="${widgetCSS}_${widget.widgetType} ${widgetCSS}_${widget.cssName}${widget.required?' required':''} form-group">
    <sling:call script="label.jsp"/>
    <div class="${widgetCSS}_path-field widget imagefield-widget widget-name_${widget.cssName}"${widget.attributes}>
        <sling:call script="preview.jsp"/>
        <div class="input-group">
            <input name="${widget.name}" class="${widgetCSS}_input form-control"
                   data-label="${widget.label}" data-i18n="${widget.i18n}"
                   type="text" value="${cpn:path(widget.model.path)}" placeholder="${cpn:path(widget.placeholder)}"/>
            <span class="${widgetCSS}_popup-button input-group-btn">
                  <button class="${widgetCSS}_select select btn btn-default" type="button"
                          title="Select the image path">...</button></span>
        </div>
    </div>
</div>

