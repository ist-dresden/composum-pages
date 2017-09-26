<%@page session="false" pageEncoding="UTF-8"%><%--
--%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%--
--%><cpp:defineFrameObjects/>
<cpp:element var="components" type="com.composum.pages.stage.model.edit.page.Components">
    <c:forEach items="${components.componentList}" var="componentType">
        <cpp:include path="${componentType.path}" resourceType="${componentType.path}" subtype="edit/tile" replaceSelectors="type"/>
    </c:forEach>
</cpp:element>
