<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<cpp:model var="model" type="com.composum.pages.stage.model.edit.FrameComponent"
           cssBase="composum-pages-stage-edit-tools-component-help-page">
    <div class="${modelCSS}_header">
        <cpn:div test="${model.developMode}" class="${modelCSS}_meta">
            <cpn:link class="${modelCSS}_path ${modelCSS}_link" href="#"
                      data-path="${model.component.path}" title="${cpn:text(model.component.path)}">
                <div class="label">${cpn:i18n(slingRequest,'Component')}</div>
                <div class="value">${cpn:text(model.component.path)}</div>
            </cpn:link>
            <cpn:link class="${modelCSS}_template ${modelCSS}_link" href="#"
                      data-path="${model.templatePath}" title="${cpn:text(model.templatePath)}">
                <div class="label">${cpn:i18n(slingRequest,'Template')}</div>
                <div class="value">${cpn:text(model.templatePath)}</div>
            </cpn:link>
            <cpn:link class="${modelCSS}_design ${modelCSS}_link" href="#"
                      data-path="${model.designPath}" title="${cpn:text(model.designPath)}">
                <div class="label">${cpn:i18n(slingRequest,'Design')}</div>
                <div class="value">${cpn:text(model.designPath)}</div>
            </cpn:link>
        </cpn:div>
        <cpn:text class="${modelCSS}_title" value="${model.titleOrName}"/>
    </div>
</cpp:model>
