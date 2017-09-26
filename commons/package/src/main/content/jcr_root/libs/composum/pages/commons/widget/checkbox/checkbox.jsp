<%@page session="false" pageEncoding="UTF-8" %><%--
--%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><cpp:defineFrameObjects/>
<div class="${widgetCssBase}_${widget.widgetType} ${widgetCssBase}_${widget.cssName} checkbox">
    <label class="${widgetCssBase}_label">
        <input name="${widget.name}" data-i18n="${widget.i18n}" ${widget.attributes}
               class="${widgetCssBase}_input widget checkbox-widget"
               type="checkbox" value="true" ${widget.model.checkedValue}/>${widget.label}<sling:call script="hint.jsp"/></label>
</div>
