<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<cpp:container var="illustration" type="com.composum.pages.components.model.illustration.Illustration"
               data-behavior="@{illustration.behavior}" style="@{illustration.style}">
    <div class="${illustrationCssBase}_image">
        <cpp:include path="image" resourceType="composum/pages/components/element/image" mode="none"/>
        <sling:call script="shapes.jsp"/>
    </div>
    <sling:call script="container.jsp"/>
</cpp:container>
