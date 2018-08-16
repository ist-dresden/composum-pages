<%@page session="false" pageEncoding="UTF-8" %><%--
--%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %><%--
--%><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%--
--%><cpp:defineFrameObjects/>
<div class="${widgetCssBase}_${widget.widgetType} ${widgetCssBase}_${widget.cssName} form-group">
    <sling:call script="label.jsp"/>
    <div class="${widgetCssBase}_wrapper composum-widgets-richtext richtext-widget widget form-control widget-name_${widget.cssName}"
         data-style="height:${widget.model.height}"
         data-label="${widget.label}" data-i18n="${widget.i18n}" ${widget.attributes}>
        <textarea class="${widgetCssBase}_value richtext-widget widget rich-editor"
                  name="${widget.name}" >${widget.model.text}</textarea>
    </div>
</div>

