<%@page session="false" pageEncoding="UTF-8" %><%--
--%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%--
--%><cpp:defineObjects/>
<cpp:container var="carousel" type="com.composum.pages.components.model.container.Carousel"
               tagId="@{carouselId}" cssAdd="@{carousel.editMode?'':'carousel slide'}"
               data-ride="@{carousel.autoStart?'carousel':''}"
               data-interval="@{carousel.interval}"
               data-pause="@{carousel.noPause?'':'hover'}">
    <%-- Indicators --%>
    <c:if test="${carousel.showIndicators}">
        <ol class="${carouselCssBase}_indicators carousel-indicators">
            <c:forEach items="${carousel.elements}" var="element" varStatus="loop">
                <li class="${carouselCssBase}_indicator-item ${loop.index == 0 ? 'active' : ''}"
                    data-target="#${carouselId}" data-slide-to="${loop.index}"></li>
            </c:forEach>
        </ol>
    </c:if>
    <%-- Wrapper for slides --%>
    <div class="${carouselCssBase}_element-set carousel-inner" role="listbox">
        <c:forEach items="${carousel.elements}" var="element" varStatus="loop">
            <div class="${carouselCssBase}_element item ${loop.index == 0 ? 'active' : ''}" data-path="${element.path}"
                 data-index="${loop.index}">
                <sling:include path="${element.path}"/>
            </div>
        </c:forEach>
    </div>
    <%-- Left and right controls --%>
    <c:if test="${carousel.useControls}">
        <a class="${carouselCssBase}_handle-prev left carousel-control" href="#${carouselId}" role="button"
           data-slide="prev">
            <span class="glyphicon glyphicon-chevron-left" aria-hidden="true"></span>
            <span class="sr-only">Previous</span>
        </a>
        <a class="${carouselCssBase}_handle-next right carousel-control" href="#${carouselId}" role="button"
           data-slide="next">
            <span class="glyphicon glyphicon-chevron-right" aria-hidden="true"></span>
            <span class="sr-only">Next</span>
        </a>
    </c:if>
</cpp:container>
