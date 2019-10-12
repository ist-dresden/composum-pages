<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineFrameObjects/>
<cpp:model var="model" type="com.composum.pages.stage.model.edit.site.SiteModel"
           cssBase="composum-pages-stage-edit-site-page" data-path="@{model.site.path}">
    <select title="Filter" class="${modelCSS}_filter form-control">
        <option value="">${cpn:i18n(slingRequest,'all')}</option>
        <c:forEach items="${model.activationStates}" var="filter">
            <option value="${filter}" ${model.filterValue==filter?'selected="selected"':''}>${cpn:i18n(slingRequest,filter)}</option>
        </c:forEach>
    </select>
</cpp:model>
