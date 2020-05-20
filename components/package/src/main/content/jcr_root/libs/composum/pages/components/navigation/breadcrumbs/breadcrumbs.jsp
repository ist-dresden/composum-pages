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
                         class="${modelCSS}_path" data-path="${page.path}"><a
                        href="${page.url}">${cpn:text(page.title)}</a></cpn:div>
            </c:forEach>
            <li class="${modelCSS}_path ${modelCSS}_current" data-path="${model.path}"><a
                    href="${model.url}">${cpn:text(model.title)}</a></li>
        </ol>
    </div>
</cpp:model>
