<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineFrameObjects/>
<div class="${widgetCSS}_${widget.widgetType} ${widgetCSS}_${widget.cssName}${widget.required?' required':''} form-group">
    <sling:call script="label.jsp"/>
    <c:if test="${!widget.blankAllowed&&widget.slingPost}">
        <input type="hidden" class="sling-post-hint" name="${widget.name}@Delete" value="true"/>
        <input type="hidden" class="sling-post-hint" name="${widget.name}@IgnoreBlanks" value="true"/>
    </c:if>
    <div class="${widgetCSS}_path-field input-group widget pathfield-widget widget-name_${widget.cssName}" ${widget.attributes}>
        <input name="${widget.name}" class="${widgetCSS}_input form-control"
               data-label="${widget.label}" data-i18n="${widget.i18n}" maxlength="${widget.model.maxlength}"
               type="text" value="${cpn:value(widget.model.path)}" placeholder="${widget.placeholder}"
               <c:if test="${widget.disabled}">disabled</c:if> />
        <span class="${widgetCSS}_popup-button input-group-btn">
                  <button class="${widgetCSS}_select select btn btn-default" type="button"
                          title="${cpn:i18n(slingRequest,'Select the repository path')}..."
                          <c:if test="${widget.disabled}">disabled</c:if>>...</button></span>
    </div>
</div>
