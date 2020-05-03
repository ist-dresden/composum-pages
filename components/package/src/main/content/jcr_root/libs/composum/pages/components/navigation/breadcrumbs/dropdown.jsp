<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineObjects/>
<cpp:model var="model" type="com.composum.pages.components.model.navigation.Breadcrumbs"
           cssBase="composum-pages-components-navigation-breadcrumbs">
    <li class="${modelCSS}_dropdown ${not empty model.currentPage.logoUrl?'has-logo':'no-logo'}">
        <c:choose>
            <c:when test="${model.submenu}">
                <a href="${'#'}" role="menuitem" aria-label="${cpn:text(model.current.title)}"
                   class="${modelCSS}_button dropdown-toggle"
                   data-toggle="dropdown">${cpn:text(model.current.title)}<i
                        class="${modelCSS}_icon fa fa-sort"></i></a>
                <ol class="${modelCSS}_dropdown-menu menu dropdown-menu">
                    <c:forEach var="item" items="${model.breadcrumbItems}" varStatus="loop">
                        <cpn:div tagName="li" test="${not empty item.title}"
                                 class="composum-pages-components-navigation-menuitem navigation-level-${loop.index}"
                                 data-path="${item.path}"><cpn:link
                                href="${item.url}"
                                class="composum-pages-components-navigation-menuitem_link" role="menuitem"><i
                                class="level-symbol fa"></i>${cpn:text(item.title)}</cpn:link></cpn:div>
                    </c:forEach>
                    <li class="composum-pages-components-navigation-menuitem navigation-level-${model.level} navigation-level-current"
                        data-path="${model.current.path}"><cpn:link
                            href="${model.current.url}"
                            class="composum-pages-components-navigation-menuitem_link" role="menuitem"><i
                            class="level-symbol fa"></i>${cpn:text(model.current.title)}</cpn:link>
                    </li>
                </ol>
            </c:when>
            <c:otherwise>
                <cpn:link href="${model.current.url}" class="${modelCSS}_link"
                          title="${model.current.title}">${cpn:text(model.current.title)}</cpn:link>
            </c:otherwise>
        </c:choose>
    </li>
</cpp:model>
