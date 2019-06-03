<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineFrameObjects/>
<cpp:model var="components" type="com.composum.pages.stage.model.edit.page.Components">
    <div class="${componentsCssBase}_filter-menu">
        <c:forEach items="${components.allCategories}" var="category">
            <div class="${componentsCssBase}_category checkbox">
                <label><input type="checkbox" value="${category}"/><span
                        class="${componentsCssBase}_label">${cpn:i18n(slingRequest,category)}</span></label>
            </div>
        </c:forEach>
    </div>
</cpp:model>
