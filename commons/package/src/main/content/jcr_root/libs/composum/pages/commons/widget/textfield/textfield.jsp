<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<cpp:defineFrameObjects/>
<div class="${widgetCSS}_${widget.widgetType} ${widgetCSS}_${widget.cssName}${widget.required?' required':''} form-group">
    <%--@elvariable id="widget" type="com.composum.pages.commons.taglib.EditWidgetTag"--%>
    <sling:call script="label.jsp"/>
    <c:if test="${!widget.blankAllowed&&widget.slingPost}">
        <input type="hidden" class="sling-post-hint" name="${widget.name}@Delete" value="true"/>
        <input type="hidden" class="sling-post-hint" name="${widget.name}@IgnoreBlanks" value="true"/>
    </c:if>
    <input <c:if test="${widget.formWidget}">name="${widget.name}"</c:if> data-label="${widget.label}"
           class="${widgetCSS}_input ${widgetCSS}_text-field widget text-field-widget form-control widget-name_${widget.cssName}"
           data-i18n="${widget.i18n}" ${widget.attributes} type="text"
           value="${cpn:value(widget.model.text)}" placeholder="${widget.placeholder}"
           <c:if test="${widget.disabled}">disabled</c:if> />
</div>
