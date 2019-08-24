<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<cpp:defineFrameObjects/>
<div class="${widgetCSS}_${widget.widgetType} ${widgetCSS}_${widget.cssName}${widget.required?' required':''} form-group">
    <sling:call script="label.jsp"/>
    <c:if test="${!widget.blankAllowed}">
        <input type="hidden" class="sling-post-hint" name="${widget.name}@Delete" value="true"/>
        <input type="hidden" class="sling-post-hint" name="${widget.name}@IgnoreBlanks" value="true"/>
    </c:if>
    <textarea name="${widget.name}" data-label="${widget.label}" data-i18n="${widget.i18n}" ${widget.attributes}
              class="${widgetCSS}_input ${widgetCSS}_text-area widget text-area-widget form-control widget-name_${widget.cssName}"
              rows="${widget.model.rows}" placeholder="${widget.placeholder}"
              <c:if test="${widget.disabled}">disabled</c:if>>${widget.model.text}</textarea>
</div>

