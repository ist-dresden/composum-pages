<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:model var="model" type="com.composum.pages.commons.model.GenericModel"
           cssBase="composum-pages-component-tile">
    <div class="${modelCSS}_title">${cpn:text(model.tileTitle)}</div>
</cpp:model>
