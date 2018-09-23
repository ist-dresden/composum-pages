<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineObjects/>
<cpp:element var="menu" type="com.composum.pages.components.model.navigation.Menu" mode="none"
             test="@{not empty menu.menuItems}"
             tagName="ul" cssAdd="nav nav-pills nav-stacked">
    <c:forEach items="${menu.menuItems}" var="item">
        <cpp:include path="${item.content.path}" resourceType="composum/pages/components/navigation/menuitem"/>
    </c:forEach>
</cpp:element>
