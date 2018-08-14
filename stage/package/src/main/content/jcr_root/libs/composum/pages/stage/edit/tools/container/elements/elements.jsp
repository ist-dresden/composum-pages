<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineFrameObjects/>
<cpp:container var="model" type="com.composum.pages.stage.model.edit.FrameContainer" mode="none"
               cssAdd="composum-pages-tools">
    <ul class="${modelCssBase}_list">
        <c:forEach items="${model.elements}" var="element" varStatus="loop">
            <li class="${modelCssBase}_element">
                <!--<input type="checkbox" class="${modelCssBase}_element-select"/-->
                <cpp:include resource="${element.resource}" subtype="edit/tile"/>
            </li>
        </c:forEach>
    </ul>
</cpp:container>
