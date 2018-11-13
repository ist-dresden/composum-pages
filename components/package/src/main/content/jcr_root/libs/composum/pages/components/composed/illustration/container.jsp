<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineObjects/>
<cpp:container var="illustration" type="com.composum.pages.components.model.composed.illustration.Illustration"
               cssBase="composum-pages-components-composed-illustration_annotations" tagName="none">
    <c:forEach items="${illustration.elements}" var="element" varStatus="loop">
        <div class="${illustrationCssBase}_element">
            <cpp:include resource="${element.resource}"/>
        </div>
    </c:forEach>
</cpp:container>