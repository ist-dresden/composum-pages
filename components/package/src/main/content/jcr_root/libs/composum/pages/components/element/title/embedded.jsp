<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<cpp:model var="title" type="com.composum.pages.components.model.text.Title"
           test="@{title.valid}">
    <div class="${titleCSS}_text">
        <cpn:text tagName="h1" class="${titleCSS}_title" value="${title.title}"/>
        <cpn:text class="${titleCSS}_subtitle" value="${title.subtitle}"/>
    </div>
</cpp:model>
