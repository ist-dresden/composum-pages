<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<cpp:model var="model" type="com.composum.pages.components.model.composed.accordion.Accordion">
    <cpp:include replaceSelectors="${model.variation}"/>
</cpp:model>
