<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<cpp:element var="model" type="com.composum.pages.components.model.time.TimeNavigator" scope="request"
             cssAdd="composum-pages-components-time-navigator" data-path="@{model.path}" data-locale="@{model.locale}">
    <sling:call script="panel.jsp"/>
</cpp:element>
