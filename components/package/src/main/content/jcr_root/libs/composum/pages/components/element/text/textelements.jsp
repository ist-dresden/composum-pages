<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineObjects/>
<cpp:model var="model" type="com.composum.pages.components.model.text.Text">
    <cpn:text tagName="${model.titleTagName}" test="${!model.hideTitle}"
              class="${modelCSS}_title" value="${model.title}"/>
    <cpn:text class="${modelCSS}_text" value="${model.text}"
              type="rich"/>
</cpp:model>