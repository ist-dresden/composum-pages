<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<cpp:defineObjects/>
<cpp:element var="model" type="com.composum.pages.components.model.text.TextImage"
             cssAdd="@{modelCSS}_@{model.floatingText?'floating':'block'} @{modelCSS}_@{model.imagePosition}">
    <sling:call script="embedded.jsp"/>
</cpp:element>
