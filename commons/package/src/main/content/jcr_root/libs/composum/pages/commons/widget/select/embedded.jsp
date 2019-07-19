<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineFrameObjects/>
<c:if test="${not empty widget.model.defaultOption}">
    <input type="hidden" class="sling-post-hint" name="${widget.name}@Delete" value="true"/>
    <input type="hidden" class="sling-post-hint" name="${widget.name}@IgnoreBlanks" value="true"/>
</c:if>
<select
        <c:if test="${widget.formWidget}">name="${widget.name}"</c:if> data-label="${widget.label}"
        data-i18n="${widget.i18n}" ${widget.attributes}
        <c:if test="${widget.disabled}">disabled</c:if>
        class="${widgetCSS}_select widget select-widget form-control widget-name_${widget.cssName}">
    <c:forEach var="option" items="${widget.model.options}">
        <option value="${option.default?'':option.value}"<c:if
                test="${option.selected}"> selected</c:if>>${cpn:text(option.label)}</option>
    </c:forEach>
</select>
