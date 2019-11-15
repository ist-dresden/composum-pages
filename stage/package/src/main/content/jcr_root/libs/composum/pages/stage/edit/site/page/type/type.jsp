<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineFrameObjects/>
<cpp:model var="model" type="com.composum.pages.stage.model.edit.site.SiteModel"
           cssBase="composum-pages-stage-edit-site-page" data-path="@{model.site.path}">
    <select title="Content Type" class="${modelCSS}_type composum-pages-tools_select form-control">
        <option value="all">${cpn:i18n(slingRequest,'Content')}</option>
        <c:forEach items="${model.contentTypes}" var="type">
            <option value="${type.key}" ${model.contentTypeValue==type.key?'selected="selected"':''}>${cpn:i18n(slingRequest,type.value)}</option>
        </c:forEach>
    </select>
</cpp:model>
