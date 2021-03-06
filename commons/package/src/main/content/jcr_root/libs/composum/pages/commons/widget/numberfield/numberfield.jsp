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
    <input type="hidden" class="sling-post-type-hint" name="${widget.name}@TypeHint" value="Long"/>
    <div class="${widgetCSS}_${widget.widgetType}_wrapper widget number-field-widget widget-name_${widget.cssName} input-group"
         data-label="${widget.label}" data-options="${widget.model.options.rule}" ${widget.attributes}>
        <cpn:div test="${!widget.required}" tagName="span" class="input-group-addon clear fa fa-times-circle"></cpn:div>
        <input name="${widget.name}" class="${widgetCSS}_input form-control" type="text"
               value="${cpn:value(widget.model.value)}"
               <c:if test="${widget.disabled}">disabled</c:if> />
        <span class="input-group-addon spinner"><span
                class="decrement fa fa-minus" title="decrement"></span><span
                class="increment fa fa-plus" title="increment"></span></span>
    </div>
</div>
