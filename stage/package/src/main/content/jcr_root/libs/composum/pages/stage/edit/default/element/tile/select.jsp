<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:model var="element" type="com.composum.pages.commons.model.Component" mode="none"
           cssBase="composum-pages-component-tile">
    <div class="${elementCssBase}">
        <sling:call script="thumbnail.jsp"/>
        <div class="${elementCssBase}_text">
            <sling:call script="_text.jsp"/>
        </div>
    </div>
</cpp:model>
