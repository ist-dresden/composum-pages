<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<cpp:element var="text" type="com.composum.pages.components.model.text.Text"
             cssAdd="@{textCssBase}_align-@{text.alignment}">
    <sling:call script="embedded.jsp"/>
</cpp:element>