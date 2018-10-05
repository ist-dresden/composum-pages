<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineObjects/>
<cpp:element var="menu" type="com.composum.pages.components.model.navigation.Menu" mode="none"
             cssBase="composum-pages-components-navigation-navbar" role="navigation"
             cssAdd="navbar navbar-default navbar-fixed-top">
    <div class="${menuCssBase}_container container-fluid">
        <div class="${menuCssBase}_header navbar-header">
            <sling:call script="branding.jsp"/>
        </div>
    </div>
</cpp:element>
