<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineObjects/>
<cpp:model var="model" type="com.composum.pages.commons.model.Page"
           cssBase="composum-pages-components-page_breadcrumbs">
    <div class="${modelCSS}">
        <ol class="${modelCSS}_list">
            <c:forEach var="page" items="${model.pagesPath}">
                <cpn:div tagName="li" test="${not empty page.title}"
                         class="${modelCSS}_path" data-path="${page.path}"><cpn:link
                        href="${page.url}">${cpn:text(page.title)}</cpn:link></cpn:div>
            </c:forEach>
            <li class="${modelCSS}_path ${modelCSS}_current" data-path="${model.path}"><cpn:link
                    href="${model.url}">${cpn:text(model.title)}</cpn:link></li>
        </ol>
    </div>
</cpp:model>
