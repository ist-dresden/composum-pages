<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineFrameObjects/>
<cpp:element var="components" type="com.composum.pages.stage.model.edit.page.Components">
    <ul class="${componentsCssBase}_list">
        <c:forEach items="${components.componentList}" var="componentType">
            <li class="${componentsCssBase}_item" draggable="true"
                data-name="${componentType.name}" data-path="${componentType.path}" data-type="${componentType.type}">
                <cpp:include path="${componentType.path}" resourceType="${componentType.path}"
                             subtype="edit/tile" replaceSelectors="type"/>
            </li>
        </c:forEach>
    </ul>
</cpp:element>
