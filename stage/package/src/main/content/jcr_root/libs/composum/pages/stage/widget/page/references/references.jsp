<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineFrameObjects/>
<div class="${widgetCssBase}_${widget.widgetType} ${widgetCssBase}_${widget.cssName} form-group">
    <sling:call script="label.jsp"/>
    <div class="${widgetCssBase}_wrapper widget page-references-widget widget-name_${widget.cssName}${empty widget.model.references?' empty':''}"
         data-label="${widget.label}" data-i18n="${widget.i18n}" ${widget.attributes}>
        <c:choose>
            <c:when test="${empty widget.model.references}">
                <div class="${widgetCssBase}_empty">
                    <cpn:text class="${widgetCssBase}_paragraph hint hint-info"
                              value="no references" i18n="true"/>
                </div>
            </c:when>
            <c:otherwise>
                <table class="${widgetCssBase}_list">
                    <thead>
                    <tr class="${widgetCssBase}_head">
                        <td class="${widgetCssBase}_checkbox"><input type="checkbox"
                                                                     class="${widgetCssBase}_input"/></td>
                        <td class="${widgetCssBase}_title">${cpn:i18n(slingRequest,'Title')}</td>
                        <td class="${widgetCssBase}_path">${cpn:i18n(slingRequest,'Path')}</td>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${widget.model.references}" var="item">
                        <tr class="${widgetCssBase}_item">
                            <td class="${widgetCssBase}_checkbox">
                                <input type="checkbox" name="${widget.name}" value="${item.path}"
                                       class="${widgetCssBase}_input"/>
                            </td>
                            <td class="${widgetCssBase}_title">${cpn:text(item.title)}</td>
                            <td class="${widgetCssBase}_path">${cpn:text(item.pathInSite)}</td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>
    </div>
</div>

