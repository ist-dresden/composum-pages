<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<cpp:model var="model" type="com.composum.pages.components.model.asset.Video">
    <video class="${modelCSS}_player" src="${model.src}"${model.controls?' controls="controls"':''}
        ${model.autoplay&&!model.editMode?' autoplay="autoplay"':''}${model.muted?' muted="muted"':''}${model.loop?' loop="loop"':''}
        ${model.posterAttr}></video>
</cpp:model>
