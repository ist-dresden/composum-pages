<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<cpp:defineObjects/>
<cpp:container var="model" type="com.composum.pages.components.model.container.Section" tagName="section"
               cssAdd="@{modelCSS}_level-@{model.level} @{model.panelCss} title-level-@{model.titleLevel}">
    <cpn:div test="${model.usePanel}" body="true" class="${modelCSS}_body panel-body">
        <cpn:anchor test="${model.setAnchor}" name="${model.anchor}" title="${model.title}"/>
        <div class="${modelCSS}_wrapper">
            <cpn:div test="${model.hasIcon}" class="${modelCSS}_icon"><i class="fa fa-${model.icon}"></i></cpn:div>
            <cpn:div test="${model.hasIcon}" body="true" class="${modelCSS}_content">
                <cpn:div test="${model.hasTitle}" class="${modelCSS}_header">
                    <cpn:text tagName="${model.titleTagName}" value="${model.title}" class="${modelCSS}_title"/>
                </cpn:div>
                <sling:call script="elements.jsp"/>
            </cpn:div>
        </div>
    </cpn:div>
</cpp:container>
