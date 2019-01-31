<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<cpp:defineObjects/>
<cpp:model var="component" type="com.composum.pages.commons.model.Component"
           cssBase="composum-pages-component-help">
    <div class="${componentCssBase}">
        <div class="${componentCssBase}_wrapper">
            <cpn:text class="${componentCssBase}_quick-help" value="${component.quickHelp}"/>
        </div>
    </div>
</cpp:model>
