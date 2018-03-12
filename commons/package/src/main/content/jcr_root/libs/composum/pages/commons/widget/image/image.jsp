<%@page session="false" pageEncoding="UTF-8" %><%--
--%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %><%--
--%><cpp:defineFrameObjects/>
<div class="${widgetCssBase}_${widget.widgetType} ${widgetCssBase}_${widget.cssName} form-group">
    <sling:call script="label.jsp"/>
    <div class="${widgetCssBase}_path-field input-group widget path-widget widget-name_${widget.cssName}" ${widget.attributes}>
        <input name="${widget.name}" class="${widgetCssBase}_input form-control"
               data-label="${widget.label}" data-i18n="${widget.i18n}"
               type="text" value="${cpn:path(widget.model.path)}" placeholder="${cpn:path(widget.placeholder)}"/>
            <span class="${widgetCssBase}_popup-button input-group-btn">
                  <button class="${widgetCssBase}_select select btn btn-default" type="button"
                          title="Select the image path">...</button>
            </span>
    </div>
</div>

