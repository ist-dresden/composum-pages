<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<cpp:defineFrameObjects/>
<input type="hidden" name="${widget.name}" class="widget hidden-widget widget-name_${widget.cssName}" data-i18n="${widget.i18n}"
       value="${cpn:text(widget.value ? widget.value : widget.model.value)}"/>

