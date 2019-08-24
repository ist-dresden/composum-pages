<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineFrameObjects/>
<cpp:container var="container" type="com.composum.pages.stage.model.edit.FrameContainer" mode="none" tagName="none"
               cssBase="composum-pages-stage-edit-tools-container-elements">
    <ul class="${containerCssBase}_list">
        <c:forEach items="${container.elements}" var="element" varStatus="loop">
            <li class="${containerCssBase}_element" draggable="true"
                data-pages-edit-reference='{"name":"${element.name}","path":"${element.path}","type":"${element.type}"}'>
                <cpp:include resource="${element.resource}" subtype="edit/tile"/>
            </li>
        </c:forEach>
    </ul>
</cpp:container>
