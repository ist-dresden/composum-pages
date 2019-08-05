<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:model var="model" type="com.composum.pages.commons.model.File"
           cssBase="composum-pages-edit-widget">
    <video class="${modelCSS}_player" src="${cpn:url(slingRequest,model.path)}" controls=""></video>
</cpp:model>
