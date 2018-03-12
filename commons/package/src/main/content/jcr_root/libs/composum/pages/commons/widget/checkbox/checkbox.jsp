<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineFrameObjects/>
<div class="${widgetCssBase}_${widget.widgetType} ${widgetCssBase}_${widget.cssName} checkbox">
    <sling:call script="hint.jsp"/>
    <label class="${widgetCssBase}_label">
        <span class="widget checkbox-widget widget-name_${widget.cssName}"
        ${widget.attributes}><c:if test="${widget.formWidget}"><input type="hidden" class="sling-post-type-hint"
                                                                      name="${widget.name}@TypeHint" value="Boolean"/>
            <input type="hidden" class="sling-post-delete-hint" name="${widget.name}@Delete" value="true"/>
            <input name="${widget.name}" data-i18n="${widget.i18n}"
                   class="${widgetCssBase}_input" type="checkbox" value="true"
                ${widget.model.checkedValue}/></c:if><c:if test="${!widget.formWidget}">
                <input data-i18n="${widget.i18n}" class="${widgetCssBase}_input" type="checkbox" value="true"
                    ${widget.model.checkedValue}/></c:if>${widget.label}</span></label>
</div>
