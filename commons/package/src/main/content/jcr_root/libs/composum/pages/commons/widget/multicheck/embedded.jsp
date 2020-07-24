<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineFrameObjects/>
<div data-label="${widget.label}" data-i18n="${widget.i18n}" ${widget.attributes}
     class="${widgetCSS} widget multicheck-widget widget-name_${widget.cssName}">
    <input type="hidden" class="sling-post-type-hint" name="${widget.name}@TypeHint" value="String[]"/>
    <input type="hidden" class="sling-post-delete-hint" name="${widget.name}@Delete"
           value="true"/>
    <c:forEach var="option" items="${widget.model.options}">
        <label class="${widgetCSS}_option"><input class="${widgetCSS}_input" type="checkbox"<c:if
                test="${widget.formWidget}"> name="${widget.name}"</c:if>
                                                  value="${cpn:text(option.value)}"<c:if
                test="${widget.disabled}"> disabled</c:if><c:if
                test="${option.selected}"> checked="checked"</c:if>/><span
                class="label-text">${cpn:text(option.label)}</span></label>
    </c:forEach>
</div>
