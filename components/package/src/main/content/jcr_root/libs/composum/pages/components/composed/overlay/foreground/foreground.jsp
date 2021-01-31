<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineObjects/>
<cpp:container var="container" type="com.composum.pages.components.model.composed.overlay.Foreground"
               test="@{container.notEmpty||container.editMode}"
               cssAdd="composum-pages-components-container @{containerCSS}_@{container.hideContent?'replace':'modify'}">
    <c:forEach items="${container.elements}" var="element">
        <cpp:include resource="${element.resource}"/>
    </c:forEach>
</cpp:container>
