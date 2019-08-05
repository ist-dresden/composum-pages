<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:element var="model" type="com.composum.pages.commons.model.GenericModel"
             cssBase="composum-pages-component-tile">
    <sling:call script="_icon.jsp"/>
    <sling:call script="_title.jsp"/>
    <cpn:text value="${model.name}" format="({})" class="${modelCSS}_name"/>
    <cpn:text value="${model.pathHint}${model.name}" class="${modelCSS}_path"/>
    <cpn:text value="${model.component.typeHint}" class="${modelCSS}_type"/>
</cpp:element>
