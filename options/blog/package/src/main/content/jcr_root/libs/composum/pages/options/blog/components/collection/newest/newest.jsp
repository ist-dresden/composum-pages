<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineObjects/>
<cpp:element var="model" type="com.composum.pages.options.blog.model.NewestArticles"
             tagName="ul">
    <c:forEach items="${model.articles}" var="article">
        <li class="${modelCSS}_item">
            <a href="${article.url}" class="${modelCSS}_link">
                <cpp:include path="${article.content.path}" mode="none"
                             resourceType="composum/pages/options/blog/components/static/intro"
                             replaceSelectors="teaser"/>
            </a>
        </li>
    </c:forEach>
</cpp:element>
