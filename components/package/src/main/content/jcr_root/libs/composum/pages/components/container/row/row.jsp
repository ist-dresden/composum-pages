<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineObjects/>
<cpp:element var="model" type="com.composum.pages.components.model.container.Row" cssAdd="row">
    <cpn:anchor test="${not empty model.anchor}" name="${model.anchor}" title="${model.title}"/>
    <cpn:div test="${not empty model.title}" class="${modelCSS}_header">
        <cpn:text tagName="${model.titleTagName}" value="${model.title}" i18n="true" class="${modelCSS}_title"/>
    </cpn:div>
    <c:forEach items="${model.columns}" var="column" varStatus="loop">
        <div class="${modelCSS}_column ${modelCSS}_column-${loop.first?'first':loop.last?'last':loop.index} col ${column}">
            <cpp:include path="column-${loop.index}" resourceType="composum/pages/components/container/row/column"/>
        </div>
    </c:forEach>
</cpp:element>
