<%@page session="false" pageEncoding="UTF-8" %><%--
--%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %><%--
--%><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%--
--%><cpp:defineFrameObjects/>
<select name="${widget.name}" data-label="${widget.label}" data-i18n="${widget.i18n}" ${widget.attributes}
        class="${widgetCssBase}_select widget select-widget form-control">
    <c:forEach var="option" items="${widget.model.options}">
        <option value="${option.value}"<c:if test="${option.selected}"> selected</c:if>>${option.label}</option>
    </c:forEach>
</select>
