<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineFrameObjects/>
<cpp:model var="model" type="com.composum.pages.stage.model.edit.site.SiteModel"
           cssBase="composum-pages-site-view-page" data-path="@{model.site.path}">
    <button type="button" title="Filter" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"
            class="fa fa-filter composum-pages-tools_button btn btn-default dropdown dropdown-toggle"><span
            class="composum-pages-tools_button-label">Filter</span></button>
    <ul class="${modelCSS}_filter composum-pages-tools_menu dropdown-menu" role="menu"
        data-value="${model.filterValue}">
        <li class="${modelCSS}_filter-value ${model.filterValue=='all'?'active':''}"><a href="#"
                                                                                        data-value="all">${cpn:i18n(slingRequest,'all')}</a>
        </li>
        <c:forEach items="${model.activationStates}" var="filter">
            <li class="${modelCSS}_filter-value ${model.filterValue==filter?'active':''}"><a
                    href="#" data-value="${filter}">${cpn:i18n(slingRequest,filter)}</a>
            </li>
        </c:forEach>
    </ul>
</cpp:model>
