<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineFrameObjects/>
<c:if test="${widget.hasLabel}"><label class="control-label ${widgetCSS}_label"><span
        class="label-text">${cpn:text(widget.label)}</span><sling:call script="labelextension.jsp"/><sling:call script="hint.jsp"/></label></c:if>
