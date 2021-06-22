<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineFrameObjects/>
<div class="composum-pages-edit-widget_datetimefield ${widgetCSS}_${widget.cssName}${widget.required?' required':''} form-group">
    <sling:call script="label.jsp"/>
    <div class="input-group widget datetimefield-widget widget-name_${widget.cssName}"
         data-format="${widget.model.momentFormat}" data-locale="${widget.requestLanguage}" ${widget.attributes}>
        <input class="${widgetCSS}_input form-control"
               data-label="${widget.label}" data-i18n="${widget.i18n}" type="text" value="${widget.model.dateValue}"
               <c:if test="${widget.disabled}">disabled</c:if> />
        <span class="${widgetCSS}_popup-button input-group-addon">
                  <span class="${widgetCSS}_select select fa fa-${widget.model.icon}"
                        title="Choose date and/or time" <c:if test="${widget.disabled}">disabled</c:if>></span></span>
    </div>
    <c:if test="${widget.slingPost}">
        <input type="hidden" class="sling-post-type-hint" name="${widget.name}@TypeHint" value="Date"/>
    </c:if>
    <input type="hidden" class="composum-pages-edit-widget_submit" name="${widget.name}"/>
</div>
