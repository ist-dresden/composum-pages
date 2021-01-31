<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineObjects/>
<cpp:model var="model" type="com.composum.pages.components.model.navigation.Breadcrumbs"
           cssBase="composum-pages-components-page_breadcrumbs">
    <div class="${modelCSS}">
        <ol class="${modelCSS}_list">
            <c:forEach var="item" items="${model.breadcrumbItems}">
                <cpn:div tagName="li" test="${not empty item.title}"
                         class="${modelCSS}_path" data-path="${item.path}"><cpn:link
                        href="${item.url}">${cpn:text(item.title)}</cpn:link></cpn:div>
            </c:forEach>
            <li class="${modelCSS}_path ${modelCSS}_current" data-path="${model.path}"><cpn:link
                    href="${model.url}">${cpn:text(model.title)}</cpn:link></li>
        </ol>
    </div>${model.jsonLdScript}
</cpp:model>
