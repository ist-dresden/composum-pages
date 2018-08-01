<%@page session="false" pageEncoding="UTF-8" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%--
--%><cpp:defineObjects/>
<cpp:model var="illustration" type="com.composum.pages.components.model.illustration.Illustration"
           cssBase="composum-pages-components-composed-illustration_shapes">
    <div class="${illustrationCssBase}">
        <c:forEach items="${illustration.elements}" var="element" varStatus="loop">
            <div class="${illustrationCssBase}_element">
                <cpp:include resource="${element.resource}" replaceSelectors="shape"/>
            </div>
        </c:forEach>
    </div>
</cpp:model>