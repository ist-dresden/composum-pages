<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineFrameObjects/>
<div class="${widgetCssBase}_${widget.widgetType} ${widgetCssBase}_${widget.cssName} form-group">
    <sling:call script="label.jsp"/>
    <div class="input-group widget datetimefield-widget widget-name_${widget.cssName}"
         data-format="${widget.model.momentFormat}" ${widget.attributes}>
        <input class="${widgetCssBase}_input form-control"
               data-label="${widget.label}" data-i18n="${widget.i18n}" type="text" value="${widget.model.dateValue}"
               <c:if test="${widget.disabled}">disabled</c:if> />
        <span class="${widgetCssBase}_popup-button input-group-addon">
                  <span class="${widgetCssBase}_select select fa fa-${widget.model.icon}"
                        title="Choose date and/or time" <c:if test="${widget.disabled}">disabled</c:if>></span></span>
    </div>
    <input type="hidden" class="sling-post-type-hint" name="${widget.name}@TypeHint" value="Date"/>
    <input type="hidden" class="${widgetCssBase}_submit" name="${widget.name}"/>
</div>
