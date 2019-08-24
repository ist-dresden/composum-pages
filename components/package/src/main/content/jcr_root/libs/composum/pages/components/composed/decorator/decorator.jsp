<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<cpp:defineObjects/>
<cpp:container var="decorator" type="com.composum.pages.components.model.composed.Decorator"
               cssAdd="@{decoratorCSS}_@{decorator.level} panel panel-@{decorator.warningLevel} title-level-@{decorator.titleLevel}">
    <div class="${decoratorCSS}_body panel-body">
        <c:forEach var="element" items="${decorator.elements}">
            <div class="${decoratorCSS}_item${decorator.hasIcon?' has-symbol':''}">
                <c:if test="${decorator.hasIcon}">
                    <div class="${decoratorCSS}_icon"><i class="fa fa-${decorator.icon}"></i></div>
                </c:if>
                <div class="${decoratorCSS}_element">
                    <cpn:text tagName="${decorator.titleTagName}" class="${decoratorCSS}_title"
                              value="${decorator.title}"/>
                    <cpp:include resource="${element.resource}"/>
                </div>
            </div>
        </c:forEach>
    </div>
</cpp:container>
