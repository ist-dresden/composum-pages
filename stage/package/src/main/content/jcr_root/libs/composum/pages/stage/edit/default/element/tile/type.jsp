<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:model var="element" type="com.composum.pages.commons.model.Component" mode="none"
           cssBase="composum-pages-component-tile" draggable="true">
    <div class="${elementCssBase}">
        <sling:call script="_text.jsp"/>
    </div>
</cpp:model>
