<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineObjects/>
<cpp:model var="model" type="com.composum.pages.components.model.navigation.Siblings"
           cssBase="composum-pages-components-navigation-siblings">
    <div class="${modelCSS}">
        <div class="${modelCSS}_prev">
            <cpn:div test="${model.hasPrevious}" class="${modelCSS}_link">
                <cpn:link href="${model.previousPage.url}"
                          title="${model.previousPage.title}">${cpn:text(model.previousPage.title)}</cpn:link>
            </cpn:div>
        </div>
        <div class="${modelCSS}_center">
            <%-- <div class="${modelCSS}_menu">
                <a href="#" role="menuitem" aria-label="${cpn:text(model.title)}"
                   class="dropdown-toggle" data-toggle="dropdown">${cpn:text(model.title)}<span
                        class="caret"></span></a>
                <ul class="${modelCSS}_menu composum-pages-components-navigation-submennu menu">
                    <c:forEach items="${model.menuItems}" var="item">
                        <cpp:include path="${item.content.path}"
                                     resourceType="composum/pages/components/navigation/menuitem"
                                     replaceSelectors="link"/>
                    </c:forEach>
                </ul>
            </div> --%>
        </div>
        <div class="${modelCSS}_next">
            <cpn:div test="${model.hasNext}" class="${modelCSS}_link">
                <cpn:link href="${model.nextPage.url}"
                          title="${model.nextPage.title}">${cpn:text(model.nextPage.title)}</cpn:link>
            </cpn:div>
        </div>
    </div>
</cpp:model>
