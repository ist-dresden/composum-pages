<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineObjects/>
<cpp:model var="model" type="com.composum.pages.components.model.time.CurrentItems" scope="request"
           cssBase="composum-pages-components-time-navigator">
    <c:forEach items="${model.items}" var="item">
        <cpp:include resource="${item.model.content.resource}" resourceType="${item.teaserType}" mode="none"/>
    </c:forEach>
</cpp:model>
