<%@page session="false" pageEncoding="UTF-8"%>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<cpp:defineFrameObjects/>

<cpp:element var="openObjects" type="com.composum.pages.stage.model.edit.site.OpenObjects" mode="none">
    <ul class="${openObjectsCssBase}_list">
        <c:forEach items="${openObjects.objectList}" var="openObject">
            <sling:include resource="${openObject}" resourceType="composum/pages/stage/edit/tools/site/unversioned/openobject"/>
        </c:forEach>
    </ul>
</cpp:element>
