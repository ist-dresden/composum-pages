<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineObjects/>
<cpp:model var="text" type="com.composum.pages.components.model.text.Text">
    <c:choose>
        <c:when test="${text.valid}">
            <cpn:text tagName="h${text.titleLevel}" class="${textCSS}_title" value="${text.title}"/>
            <cpn:text class="${textCSS}_text" value="${text.text}"
                      type="rich"/>
        </c:when>
        <c:otherwise>
            <cpp:include test="${text.editMode}" replaceSelectors="placeholder"/>
        </c:otherwise>
    </c:choose>
</cpp:model>