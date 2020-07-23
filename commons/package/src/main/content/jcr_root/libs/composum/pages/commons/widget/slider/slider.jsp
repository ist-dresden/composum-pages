<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineFrameObjects/>
<div class="${widgetCSS}_${widget.widgetType} ${widgetCSS}_${widget.cssName}${widget.required?' required':''} form-group">
    <sling:call script="label.jsp"/>
    <input type="hidden" class="sling-post-hint" name="${widget.name}@Delete" value="true"/>
    <input type="hidden" class="sling-post-hint" name="${widget.name}@IgnoreBlanks" value="true"/>
    <input type="hidden" class="sling-post-type-hint" name="${widget.name}@TypeHint" value="Decimal"/>
    <div class="${widgetCSS}_${widget.widgetType}_wrapper widget slider-widget" ${widget.attributes}
         data-label="${widget.label}" data-i18n="${widget.i18n}">
        <input name="${widget.name}"
               class="${widgetCSS}_input slider-widget_input form-control widget-name_${widget.cssName}" type="text"
               data-slider-min="${widget.model.options.min}" data-slider-max="${widget.model.options.max}"
               data-slider-step="${widget.model.options.step}" value="${cpn:text(widget.model.value)}"
               <c:if test="${widget.disabled}">disabled</c:if> />
    </div>
</div>
