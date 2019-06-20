<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<cpp:defineFrameObjects/>
<span class="input-group-addon ${widgetCSS}_${widget.widgetType}_icon"><i
        class="fa fa-${cpn:text(widget.model.text)}" data-value-class="fa fa-$"></i></span>