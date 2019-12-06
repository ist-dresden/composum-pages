<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineObjects/>
<cpp:model var="model" type="com.composum.pages.components.model.time.NewsNavigator" scope="request"
           cssBase="composum-pages-components-time-navigator">
    <c:forEach items="${model.items}" var="news">
        <cpp:include resource="${news.content.resource}" mode="none"
                     resourceType="composum/pages/components/time/news/teaser"/>
    </c:forEach>
</cpp:model>
