<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<cpp:defineObjects/>
<cpp:container var="model" type="com.composum.pages.commons.model.Container" tagName="section">
    <cpn:anchor test="${not empty model.properties.anchor}" name="${model.properties.anchor}" title="${model.title}"/>
    <cpn:div test="${not empty model.title}" class="${modelCSS}_header">
        <cpn:text tagName="${model.titleTagName}" value="${model.title}" i18n="true" class="${modelCSS}_title"/>
    </cpn:div>
    <sling:call script="elements.jsp"/>
</cpp:container>
