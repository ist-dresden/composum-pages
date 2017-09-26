<%@page session="false" pageEncoding="UTF-8" %><%--
--%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %><%--
--%><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%--
--%><cpp:defineFrameObjects/>
<div class="${widgetCssBase}_${widget.widgetType} ${widgetCssBase}_${widget.cssName} form-group">
    <sling:call script="label.jsp"/>
    <div class="widget radio-group-widget form-control">
        <c:forEach var="option" items="${widget.model.options}">
            <div class="radio-inline"><label><input type="radio" name="${widget.name}" value="${option.value}" <c:if
                    test="${option.selected}"> selected</c:if> />${option.label}</label></div>
        </c:forEach>
    </div>
</div>
