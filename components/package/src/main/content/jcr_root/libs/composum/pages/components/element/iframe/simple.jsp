<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineObjects/>
<cpp:element id="@{modelId}" var="model" type="com.composum.pages.components.model.element.IFrame"
             test="@{model.valid||model.editMode}" cssAdd="@{modelCSS}_@{model.style}">
    <cpn:text tagName="${model.titleTagName}" class="composum-pages-components-element-text_title"
              value="${model.title}"/>
    <div class="${modelCSS}_wrapper">
        <cpp:include replaceSelectors="${model.editMode?'placeholder':'view'}"/>
    </div>
    <cpn:text class="${modelCSS}_footer" value="${model.copyright}"/>
</cpp:element>
