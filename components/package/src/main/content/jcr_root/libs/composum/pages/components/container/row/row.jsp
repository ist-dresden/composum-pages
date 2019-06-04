<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineObjects/>
<cpp:element var="row" type="com.composum.pages.components.model.container.Row" cssAdd="row">
    <c:forEach items="${row.columns}" var="column" varStatus="loop">
        <div class="${rowCssBase}_column ${rowCssBase}_column-${loop.first?'first':loop.last?'last':loop.index} ${column}">
            <cpp:include path="column-${loop.index}" resourceType="composum/pages/components/container/row/column"/>
        </div>
    </c:forEach>
</cpp:element>
