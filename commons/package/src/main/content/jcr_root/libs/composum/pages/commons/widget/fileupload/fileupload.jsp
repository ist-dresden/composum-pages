<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<cpp:defineFrameObjects/>
<div class="${widgetCssBase}_${widget.widgetType} ${widgetCssBase}_${widget.cssName} form-group">
    <sling:call script="label.jsp"/>
    <input name="${widget.name}" type="file" ${widget.attributes}
           class="${widgetCssBase}_fileupload widget file-upload-widget ${widgetCssBase}_input form-control widget-name_${widget.cssName}"
           data-options="browse:${cpn:i18n(slingRequest,'Browse')} ...:${cpn:i18n(slingRequest,'browse file system to find a file')},remove:${cpn:i18n(slingRequest,'Remove')}:${cpn:i18n(slingRequest,'remove the chosen file')}"/>
</div>
