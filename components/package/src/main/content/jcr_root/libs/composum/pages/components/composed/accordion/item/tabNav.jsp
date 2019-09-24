<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<cpp:element var="model" type="com.composum.pages.components.model.composed.accordion.AccordionItem"
             tagName="li" role="presentation" mode="none"
             cssBase="composum-pages-components-composed-accordion-tab" cssAdd="@{model.initialOpen?'active':''}"><a
        role="tab" data-toggle="tab" href="#${modelId}_content" aria-controls="${modelId}_content"><cpn:text
        value="${model.title}"/></a></cpp:element>
