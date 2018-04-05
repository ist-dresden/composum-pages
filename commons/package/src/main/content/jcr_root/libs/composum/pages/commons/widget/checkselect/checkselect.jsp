<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineFrameObjects/><%-- the 'checkselect' (checkbox) widget form template --%>
<div class="${widgetCssBase}_${widget.widgetType} ${widgetCssBase}_${widget.cssName} checkbox">
    <sling:call script="hint.jsp"/><%-- place hints in the 'label level'; on top of the checkbox --%>
    <label class="${widgetCssBase}_label"><%-- for checkboxes the label is the checkbox label itself --%>
        <%-- this 'span' is the widget 'div'; the hook for the widgets JS view --%>
        <span class="widget ${widget.widgetType}-widget widget-name_${widget.cssName}" data-name="${widget.name}"
              data-options='${widget.model.optionsData}' ${widget.attributes}><c:if test="${widget.formWidget}"><%--
               if the widget is embedded in a Sling POST form generate all hidden hints for the POST servlet
           --%><input type="hidden" class="sling-post-type-hint" name="${widget.name}@TypeHint" value="String"/>
            <c:if test="${widget.model.removable}"><%-- if 'removable' add delete hint
           --%><input type="hidden" class="sling-post-delete-hint" name="${widget.name}@Delete"
                      value="true"/></c:if><%-- and now the input field... --%>
            <input <%-- set name attribute for the input field only box should be checked --%>
                    <c:if test="${widget.model.checked}">name="${widget.name}"</c:if> data-i18n="${widget.i18n}"
                    class="${widgetCssBase}_input" type="checkbox" value="${widget.model.inputValue}"
                ${widget.model.checkedValue}/></c:if><c:if test="${!widget.formWidget}"><%--
               not a Sling POST form: generate a simple checkbox input field...
           --%><input data-i18n="${widget.i18n}" class="${widgetCssBase}_input" type="checkbox"
                      <c:if test="${widget.model.checked}">name="${widget.name}"</c:if>
                      value="${widget.model.inputValue}" ${widget.model.checkedValue}/></c:if><%--
           in each case - the label of the checkbox... --%>${widget.label}
    <c:if test="${widget.model.hasSecondValue}"><%--
           an additional a hidden field for the 'unchecked' value if such an option is configured
           --%><input type="hidden" class="${widget.widgetType}-widget_second" <%--
                      the name attribute is set for the selected value... (this or the input field)--%>
                      <c:if test="${!widget.model.checked}">name="${widget.name}"</c:if>
                      value="${widget.model.secondValue}"/></c:if></span></label>
</div>
