<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineFrameObjects/>
<c:choose>
    <c:when test="${not empty widget.value}">
        <input type="hidden" name="${widget.name}" class="widget hidden-widget widget-name_${widget.cssName}"
               data-i18n="${widget.i18n}" value="${cpn:text(widget.value)}"/></c:when>
    <c:otherwise>
        <c:forEach items="${widget.model.values}" var="value">
            <input type="hidden" name="${widget.name}" class="widget hidden-widget widget-name_${widget.cssName}"
                   data-i18n="${widget.i18n}" value="${cpn:text(value)}"/>
        </c:forEach>
    </c:otherwise>
</c:choose>

