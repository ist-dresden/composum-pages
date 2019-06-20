<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineFrameObjects/>
<div class="${widgetCSS}_${widget.widgetType} ${widgetCSS}_${widget.cssName} form-group">
    <sling:call script="label.jsp"/>
    <div class="${widgetCSS}_combobox widget combobox-widget widget-name_${widget.cssName} input-group" ${widget.attributes}>
        <sling:call script="left.jsp"/>
        <input
                <c:if test="${widget.formWidget}">name="${widget.name}"</c:if> data-label="${widget.label}"
                class="${widgetCSS}_input form-control" data-i18n="${widget.i18n}" type="text"
                value="${cpn:text(widget.model.text)}" placeholder="${cpn:text(widget.placeholder)}"
                <c:if test="${widget.disabled}">disabled</c:if> />
        <div class="input-group-btn">
            <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true"
                    <c:if test="${widget.disabled}">disabled</c:if>><i class="fa fa-caret-down"></i></button>
            <ul class="dropdown-menu dropdown-menu-right">
                <c:forEach var="option" items="${widget.model.options}">
                    <li class="${option.selected?'active':''}"
                        data-value="${option.value}"><a href="#">${cpn:text(option.label)}</a></li>
                </c:forEach>
            </ul>
        </div>
    </div>
</div>
