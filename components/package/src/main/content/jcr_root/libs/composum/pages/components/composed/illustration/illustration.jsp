<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineObjects/>
<cpp:container var="illustration" type="com.composum.pages.components.model.composed.illustration.Illustration"
               data-behavior="@{illustration.behavior}" style="@{illustration.style}">
    <div class="${illustrationCSS}_image">
        <cpp:dropZone property="image/imageRef" filter="asset:image">
            <cpp:include path="image" resourceType="composum/pages/components/element/image" mode="none"/>
        </cpp:dropZone>
        <sling:call script="shapes.jsp"/>
    </div>
    <c:if test="${illustration.editMode}">
        <sling:call script="container.jsp"/>
    </c:if>
</cpp:container>
