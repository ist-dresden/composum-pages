<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<cpp:model var="model" type="com.composum.pages.stage.model.edit.page.HelpPage">
    <div class="${modelCSS}_header">
        <cpn:text class="${modelCSS}_title" value="${model.component.titleOrName}"/>
    </div>
</cpp:model>
