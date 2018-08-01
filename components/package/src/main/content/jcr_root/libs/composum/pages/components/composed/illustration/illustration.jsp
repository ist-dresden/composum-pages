<%@page session="false" pageEncoding="UTF-8" %><%--
--%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %><%--
--%><%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%--
--%><cpp:defineObjects/>
<cpp:element var="illustration" type="com.composum.pages.components.model.illustration.Illustration"
               data-behavior="@{illustration.behavior}" style="@{illustration.style}">
    <div class="${illustrationCssBase}_image">
        <cpp:include path="image" resourceType="composum/pages/components/element/image"/>
        <sling:call script="shapes.jsp"/>
        <sling:call script="container.jsp"/>
    </div>
</cpp:element>
