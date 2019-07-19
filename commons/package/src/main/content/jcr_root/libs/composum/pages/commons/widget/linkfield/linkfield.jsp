<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<cpp:defineFrameObjects/>
<div class="${widgetCSS}_${widget.widgetType} ${widgetCSS}_${widget.cssName}${widget.required?' required':''} form-group">
    <sling:call script="label.jsp"/>
    <c:if test="${!widget.blankAllowed}">
        <input type="hidden" class="sling-post-hint" name="${widget.name}@Delete" value="true"/>
        <input type="hidden" class="sling-post-hint" name="${widget.name}@IgnoreBlanks" value="true"/>
    </c:if>
    <div class="${widgetCSS}_link-field input-group widget linkfield-widget widget-name_${widget.cssName}" ${widget.attributes}
         title="${cpn:i18n(slingRequest,'Select Target')}"
         data-label="${cpn:i18n(slingRequest,'Link or select(ed) path')}">
        <input name="${widget.name}" class="${widgetCSS}_input form-control" type="text"
               data-label="${widget.label}" data-i18n="${widget.i18n}"
               value="${cpn:value(widget.model.path)}" placeholder="${cpn:value(widget.placeholder)}"/>
        <span class="${widgetCSS}_popup-button input-group-btn">
              <button class="${widgetCSS}_select select btn btn-default" type="button"
                      title="${cpn:i18n(slingRequest,'Select the repository path')}...">...</button></span>
    </div>
</div>
