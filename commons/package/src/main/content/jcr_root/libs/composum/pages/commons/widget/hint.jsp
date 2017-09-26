<%@page session="false" pageEncoding="UTF-8" %><%--
--%><%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%--
--%><cpp:defineFrameObjects/>
<c:if test="${widget.hasHint}"><cpn:text tagName="span" tagClass="${widgetCssBase}_hint widget-hint"
                                         i18n="true" value="${widget.hint}" type="value"/></c:if>
