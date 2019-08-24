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
    <div class="${widgetCSS}_wrapper composum-widgets-richtext richtext-widget widget form-control widget-name_${widget.cssName}"
         data-style="height:${widget.model.height}"
         data-label="${widget.label}" data-i18n="${widget.i18n}" ${widget.attributes}>
        <textarea class="${widgetCSS}_value richtext-widget widget rich-editor"
                  name="${widget.name}" >${widget.model.text}</textarea>
    </div>
</div>

