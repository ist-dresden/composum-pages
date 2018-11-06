<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineObjects/>
<cpp:container var="container" type="com.composum.pages.commons.model.Container"
               tagName="ul">
    <c:forEach items="${container.elements}" var="element" varStatus="loop">
        <li class="${containerCssBase}_item">
            <cpp:include resource="${element.resource}"/>
        </li>
    </c:forEach>
</cpp:container>