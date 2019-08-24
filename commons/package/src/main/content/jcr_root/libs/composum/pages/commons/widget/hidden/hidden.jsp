<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineFrameObjects/>
<c:choose><c:when test="${widget.model.collection}">
    <c:forEach items="${widget.model.values}" var="value">
        <input type="hidden" name="${widget.name}" class="widget hidden-widget widget-name_${widget.cssName}"
               data-i18n="${widget.i18n}" value="${value}"/>
    </c:forEach></c:when><c:otherwise>
    <input type="hidden" name="${widget.name}" class="widget hidden-widget widget-name_${widget.cssName}"
           data-i18n="${widget.i18n}" value="${widget.value}"/>
</c:otherwise>
</c:choose>

