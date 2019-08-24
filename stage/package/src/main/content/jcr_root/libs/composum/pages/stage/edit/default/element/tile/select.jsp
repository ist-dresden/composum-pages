<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:model var="model" type="com.composum.pages.commons.model.GenericModel"
           cssBase="composum-pages-component-tile">
    <div class="${modelCSS}">
        <sling:call script="thumbnail.jsp"/>
        <div class="${modelCSS}_text">
            <sling:call script="_icon.jsp"/>
            <sling:call script="_title.jsp"/>
            <cpn:text value="${model.name}" format="{Message}({0})" class="${modelCSS}_name"/>
            <cpn:text value="${model.component.typeHint}" class="${modelCSS}_type"/>
            <cpn:text value="${model.description}" class="${modelCSS}_description"
                      title="${cpn:text(model.description)}"/>
        </div>
    </div>
</cpp:model>
