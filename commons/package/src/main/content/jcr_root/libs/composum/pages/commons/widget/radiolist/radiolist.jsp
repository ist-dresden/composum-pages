<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineFrameObjects/>
<div class="${widgetCSS}_${widget.widgetType} ${widgetCSS}_${widget.cssName} form-group">
    <sling:call script="label.jsp"/>
    <div class="widget radio-group-widget form-control widget-name_${widget.cssName}">
        <c:forEach var="option" items="${widget.model.options}">
            <div class="${widgetCSS}_radiolist-item">
                <div class="radio-inline"><label><input
                        type="radio" <c:if test="${widget.formWidget}">name="${widget.name}"</c:if>
                        value="${option.value}" <c:if test="${option.selected}"> checked="checked"</c:if> />${option.label}</label></div>
            </div>
        </c:forEach>
    </div>
</div>
