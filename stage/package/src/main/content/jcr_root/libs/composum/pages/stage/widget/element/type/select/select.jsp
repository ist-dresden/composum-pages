<%@page session="false" pageEncoding="UTF-8" %>
<%--
    the markup of the element-type-select-widget frame (is loading content via AJAX)

    /libs/composum/pages/stage/widget/element
--%>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineFrameObjects/>
<div class="${widgetCssBase}_${widget.widgetType} ${widgetCssBase}_${widget.cssName} form-group">
    <sling:call script="label.jsp"/>
    <div class="${widgetCssBase}_wrapper widget element-type-select-widget widget-name_${widget.cssName}"
         data-label="${widget.label}" data-i18n="${widget.i18n}" ${widget.attributes}
         data-name="${widget.name}" data-container="${widget.model.containerRef}">
        <div class="${widgetCssBase}_toolbar">
            <div class="${widgetCssBase}_filter">
                <button type="button"
                        class="${widgetCssBase}_filter-toggle fa fa-filter btn btn-sm btn-default"
                        title="${cpn:i18n(slingRequest,'Category Filter')}"></button>
                <div class="${widgetCssBase}_filter-menu">
                    <c:forEach items="${widget.model.components.allCategories}" var="category">
                        <div class="${widgetCssBase}_category checkbox">
                            <label><input type="checkbox" value="${category}"/><span
                                    class="${widgetCssBase}_label">${cpn:i18n(slingRequest,category)}</span></label>
                        </div>
                    </c:forEach>
                </div>
            </div>
            <div class="${widgetCssBase}_search">
                <div class="input-group input-group-sm">
                    <span class="${widgetCssBase}_search-reset input-group-addon fa fa-times-circle"></span>
                    <input class="${widgetCssBase}_search-field form-control" type="text"
                           title="${cpn:i18n(slingRequest,'Search Term')}"
                           placeholder="${cpn:i18n(slingRequest,'search...')}"/>
                    <span class="${widgetCssBase}_search-action input-group-addon fa fa-search"></span>
                </div>
            </div>
        </div>
        <div class="${widgetCssBase}_select-content">
            <%-- AJAX: <sling:include replaceSelectors="content"/> --%>
        </div>
    </div>
</div>

