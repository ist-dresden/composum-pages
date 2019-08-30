<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineObjects/>
<cpp:container var="model" type="com.composum.pages.components.model.composed.Carousel"
               tagId="@{modelId}" cssAdd="@{model.editMode?'':'carousel slide'}"
               data-ride="model.autoStart?'carousel':''}"
               data-interval="@{model.autoStart?model.interval:'false'}"
               data-pause="@{model.noPause?'':'hover'}">
    <%-- Indicators --%>
    <c:if test="${model.showIndicators}">
        <ol class="${modelCSS}_indicators carousel-indicators">
            <c:forEach items="${model.elements}" var="element" varStatus="loop">
                <li class="${modelCSS}_indicator-item ${loop.index == 0 ? 'active' : ''}"
                    data-target="#${modelId}" data-slide-to="${loop.index}"></li>
            </c:forEach>
        </ol>
    </c:if>
    <%-- Wrapper for slides --%>
    <div class="${modelCSS}_element-set carousel-inner" role="listbox">
        <c:forEach items="${model.elements}" var="element" varStatus="loop">
            <div class="${modelCSS}_element item ${loop.index == 0 ? 'active' : ''}" data-path="${element.path}"
                 data-index="${loop.index}">
                <cpp:include resource="${element.resource}"/>
            </div>
        </c:forEach>
    </div>
    <%-- Left and right controls --%>
    <c:if test="${model.useControls}">
        <a class="${modelCSS}_handle-prev left carousel-control" href="#${modelId}" role="button"
           data-slide="prev">
            <span class="glyphicon glyphicon-chevron-left" aria-hidden="true"></span>
            <span class="sr-only">${cpn:i18n(slingRequest,'Previous')}</span>
        </a>
        <a class="${modelCSS}_handle-next right carousel-control" href="#${modelId}" role="button"
           data-slide="next">
            <span class="glyphicon glyphicon-chevron-right" aria-hidden="true"></span>
            <span class="sr-only">${cpn:i18n(slingRequest,'Next')}</span>
        </a>
    </c:if>
</cpp:container>
