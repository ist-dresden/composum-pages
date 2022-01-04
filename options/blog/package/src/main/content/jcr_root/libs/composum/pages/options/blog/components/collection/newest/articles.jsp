<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineObjects/>
<cpp:model var="model" type="com.composum.pages.options.blog.model.NewestArticles" mode="none">
    <ul class="${modelCSS}_list">
        <c:forEach items="${model.articles}" var="article">
            <li class="${modelCSS}_list-item">
                <a href="${article.url}" class="${modelCSS}_link">
                    <cpp:include path="${article.content.path}" mode="none"
                                 resourceType="composum/pages/options/blog/components/static/intro"
                                 replaceSelectors="teaser"/>
                </a>
            </li>
        </c:forEach>
    </ul>
</cpp:model>
