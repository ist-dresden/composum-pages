<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<cpp:model var="model" type="com.composum.pages.components.model.time.NavigatorModel" scope="request"
           cssBase="composum-pages-components-time-navigator">
    <sling:call script="header.jsp"/>
    <div class="${modelCSS}_items">
        <sling:call script="items.jsp"/>
    </div>
</cpp:model>
