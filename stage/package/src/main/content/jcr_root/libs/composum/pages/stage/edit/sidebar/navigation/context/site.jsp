<%@page session="false" pageEncoding="UTF-8"%>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineFrameObjects/>
<cpp:model var="model" type="com.composum.pages.stage.model.edit.site.SiteModel" mode="none">
    <c:if test="${model.site!=null}">
        <sling:call script="actions.jsp"/>
        <cpp:include resource="${model.site.resource}" subtype="edit/tile" replaceSelectors="status"/>
    </c:if>
</cpp:model>
