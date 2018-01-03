<%@page session="false" pageEncoding="UTF-8" %><%--
--%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %><%--
--%><%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%--
--%><cpp:defineFrameObjects/>
<div class="${widgetCssBase}_${widget.widgetType} ${widgetCssBase}_${widget.cssName} form-group">
    <sling:call script="label.jsp"/>
    <div class="${widgetCssBase}_wrapper widget site-templates-widget">
        <div class="${widgetCssBase}_toolbar">
            <div class="${widgetCssBase}_search">
            </div>
        </div>
        <c:if test="${empty widget.model.templates}">
            <div class="${widgetCssBase}_empty">
                <cpn:text tagClass="${widgetCssBase}_paragraph alert alert-warning"
                          value="no Site templates available" i18n="true"/>
            </div>
        </c:if>
        <ul class="${widgetCssBase}_list">
            <c:forEach items="${widget.model.templates}" var="site">
                <li class="${widgetCssBase}_site">
                    <input type="radio" name="${widget.name}" value="${site.path}" class="${widgetCssBase}_radio"/>
                    <cpp:include resource="${site.resource}" subtype="edit/tile" replaceSelectors="list"/>
                </li>
            </c:forEach>
        </ul>
        <div class="${widgetCssBase}_site ${widgetCssBase}_no-template">
            <input type="radio" name="template" value="" class="${widgetCssBase}_radio"/>
            <cpn:link class="composum-pages-stage-site_tile" href="#">
                <cpp:include resourceType="composum/pages/stage/edit/default/site/thumbnail"/>
                <div class="composum-pages-stage-site_tile_text">
                    <cpn:text tagName="h3" value="no template" i18n="true" tagClass="composum-pages-stage-site_tile_title"/>
                    <cpn:text tagName="div" value="<p>Create a new site without a template.</p>"
                              type="rich" i18n="true" tagClass="composum-pages-stage-site_tile_description"/>
                </div>
            </cpn:link>
        </div>
    </div>
</div>

