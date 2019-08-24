<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineFrameObjects/>
<cpp:element var="components" type="com.composum.pages.stage.model.edit.page.Components" mode="none">
    <div class="${componentsCssBase}_categories">
        <c:forEach items="${components.categories}" var="category">
            <div class="${componentsCssBase}_category">
                <a class="${componentsCssBase}_category_title" href="#${componentsCssBase}_category_${category}"
                   data-toggle="collapse"><i
                        class="fa fa-angle-down"></i><span>${cpn:i18n(slingRequest,category)}</span></a>
                <ul class="${componentsCssBase}_list collapse in" id="${componentsCssBase}_category_${category}">
                    <c:forEach items="${components.components[category]}" var="componentType">
                        <li class="${componentsCssBase}_item" draggable="true"
                            data-pages-edit-reference='{"name":"${componentType.name}","path":"${componentType.path}","type":"${componentType.type}"}'>
                            <cpp:include path="${componentType.path}" resourceType="${componentType.path}"
                                         subtype="edit/tile" replaceSelectors="type"/>
                        </li>
                    </c:forEach>
                </ul>
            </div>
        </c:forEach>
    </div>
</cpp:element>
