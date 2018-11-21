<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<cpp:container var="section" type="com.composum.pages.components.model.container.Section"
               tagName="section">
    <cpn:anchor test="${not empty section.anchor}" name="${section.anchor}"/>
    <cpp:include path="_title" resourceType="composum/pages/components/element/title"/>
    <sling:call script="elements.jsp"/>
</cpp:container>