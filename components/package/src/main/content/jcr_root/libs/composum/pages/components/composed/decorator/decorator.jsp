<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineObjects/>
<cpp:container var="decorator" type="com.composum.pages.components.model.decorator.Decorator"
               cssAdd="@{decoratorCssBase}_@{decorator.level} panel panel-@{decorator.level}">
    <div class="${decoratorCssBase}_body panel-body">
        <c:forEach var="element" items="${decorator.elements}">
            <div class="${decoratorCssBase}_item${decorator.hasIcon?' has-symbol':''}">
                <c:if test="${decorator.hasIcon}">
                    <div class="${decoratorCssBase}_icon"><i class="fa fa-${decorator.icon}"></i></div>
                </c:if>
                <div class="${decoratorCssBase}_element">
                    <cpp:include resource="${element.resource}"/>
                </div>
            </div>
        </c:forEach>
    </div>
</cpp:container>
