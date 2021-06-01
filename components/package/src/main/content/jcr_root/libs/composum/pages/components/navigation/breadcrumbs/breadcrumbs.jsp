<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineObjects/>
<cpp:model var="model" type="com.composum.pages.components.model.navigation.Breadcrumbs"
           cssBase="composum-pages-components-navigation-breadcrumbs">
    <div class="${modelCSS}">
        <c:if test="${model.useful}">
            <ol class="${modelCSS}_list">
                <c:forEach var="item" items="${model.breadcrumbItems}">
                    <li class="${modelCSS}_item${item.current?' current-item':''}" data-path="${item.path}"><cpn:link
                            href="${item.url}" title="${item.title}">${cpn:text(item.label)}</cpn:link></li>
                </c:forEach>
            </ol>
        </c:if>
    </div>
    ${model.jsonLdScript}
</cpp:model>
