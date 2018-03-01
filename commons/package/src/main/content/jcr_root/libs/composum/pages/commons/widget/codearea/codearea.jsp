<%@page session="false" pageEncoding="UTF-8" %><%--
--%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %><%--
--%><cpp:defineFrameObjects/>
<div class="${widgetCssBase}_${widget.widgetType} ${widgetCssBase}_${widget.cssName} form-group">
    <sling:call script="label.jsp"/>
    <div class="${widgetCssBase}_editor codearea-widget widget code-editor form-control widget-name_${widget.name}"
         data-name="${widget.name}" data-height="${widget.model.height}"
         data-label="${widget.label}" data-i18n="${widget.i18n}" ${widget.attributes}>${cpn:text(widget.model.text)}</div>
</div>
