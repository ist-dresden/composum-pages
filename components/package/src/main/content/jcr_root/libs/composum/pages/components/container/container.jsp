<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<cpp:container var="container" type="com.composum.pages.commons.model.Container"
               cssBase="composum-pages-components-container">
    <sling:call script="elements.jsp"/>
</cpp:container>
