<%@page session="false" pageEncoding="UTF-8" %><%--
--%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %><%--
--%><cpp:defineFrameObjects/>
<div class="${widgetCssBase}_${widget.widgetType} ${widgetCssBase}_${widget.cssName} form-group">
    <sling:call script="label.jsp"/>
    <div class="${widgetCssBase}_link-field input-group widget linkfield-widget widget-name_${widget.cssName}" ${widget.attributes}
         title="Select Link" data-label="Link or select(ed) path">
        <input name="${widget.name}" class="${widgetCssBase}_input form-control" type="text"
               data-label="${widget.label}" data-i18n="${widget.i18n}"
               value="${cpn:value(widget.model.path)}" placeholder="${cpn:value(widget.placeholder)}"/>
        <span class="${widgetCssBase}_popup-button input-group-btn">
              <button class="${widgetCssBase}_select select btn btn-default" type="button"
                      title="Select the repository path">...</button></span>
    </div>
</div>

