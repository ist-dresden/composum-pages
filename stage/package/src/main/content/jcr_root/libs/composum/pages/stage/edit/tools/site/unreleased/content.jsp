<%@page session="false" pageEncoding="UTF-8"%>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<cpp:defineFrameObjects/>

<cpp:element var="finishedObjects" type="com.composum.pages.stage.model.edit.site.FinishedObjects" mode="none">
    <ul class="${finishedObjectsCssBase}_list">
        <c:forEach items="${finishedObjects.objectList}" var="finishedObject">
            <sling:include resource="${finishedObject}" resourceType="composum/pages/stage/edit/tools/site/unreleased/finishedobject"/>
        </c:forEach>
    </ul>
</cpp:element>
