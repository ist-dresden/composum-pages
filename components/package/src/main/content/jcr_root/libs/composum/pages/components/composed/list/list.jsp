<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineObjects/>
<cpp:container var="list" type="com.composum.pages.components.model.listtype.ListType">
    <${list.type} class="${listCssBase}_list">
    <c:forEach var="item" items="${list.elements}">
        <li class="${listCssBase}_list-item">
            <cpp:include path="${item.path}"/>
        </li>
    </c:forEach>
    </${list.type}>
</cpp:container>
