<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineObjects/>
<cpp:model var="model" type="com.composum.pages.commons.model.LanguageRoot" test="@{not empty model.alternatives}">
    <c:forEach items="${model.alternatives}" var="alt">
        <link rel="alternate" hreflang="${cpn:text(alt.language.key)}" href="${cpn:url(slingRequest,alt.url)}"/>
    </c:forEach>
</cpp:model>
