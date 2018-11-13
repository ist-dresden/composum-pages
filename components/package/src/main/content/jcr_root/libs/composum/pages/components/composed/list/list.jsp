<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineObjects/>
<cpp:container var="list" type="com.composum.pages.components.model.composed.List"
               tagName="@{list.type}">
    <c:forEach items="${list.elements}" var="item">
        <li class="${listCssBase}_list-item"><cpp:include resource="${item.resource}"/></li>
    </c:forEach>
</cpp:container>
