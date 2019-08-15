<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<cpp:model var="model" type="com.composum.pages.components.model.text.Title"
           test="@{model.valid}">
    <div class="${modelCSS}_text">
        <cpn:text tagName="${model.titleTagName}" class="${modelCSS}_title" value="${model.title}"/>
        <cpn:text class="${modelCSS}_subtitle" value="${model.subtitle}"/>
    </div>
</cpp:model>
