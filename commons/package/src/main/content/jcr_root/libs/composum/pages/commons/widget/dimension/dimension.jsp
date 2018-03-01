<%@page session="false" pageEncoding="UTF-8" %><%--
--%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %><%--
--%><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%--
--%><cpp:defineFrameObjects/>
<div class="${widgetCssBase}_${widget.widgetType} ${widgetCssBase}_${widget.cssName} form-group widget-name_${widget.name}">
    <sling:call script="label.jsp"/>
    <div class="form-inline">
        <input name="${widget.name}.width" data-i18n="${widget.i18n}" size="7"
               class="${widgetCssBase}_input ${widgetCssBase}_text-field widget text-field-widget form-control"
               type="text" value="${cpn:text(widget.model.width)}" placeholder="${cpn:text(widget.model.defaultWidth)}"
               <c:if test="${widget.disabled}">disabled</c:if> />&nbsp;,&nbsp;<input
            name="${widget.name}.height" data-i18n="${widget.i18n}" size="7"
            class="${widgetCssBase}_input ${widgetCssBase}_text-field widget text-field-widget form-control"
            type="text" value="${cpn:text(widget.model.height)}" placeholder="${cpn:text(widget.model.defaultHeight)}"
            <c:if test="${widget.disabled}">disabled</c:if> />
    </div>
</div>

