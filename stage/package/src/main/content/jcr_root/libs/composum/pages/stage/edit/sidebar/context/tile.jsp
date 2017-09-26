<%@page session="false" pageEncoding="UTF-8"%><%--
--%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %><%--
--%><%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><cpp:defineFrameObjects/>
<cpp:model var="element" type="com.composum.pages.stage.model.edit.FrameElement">
    <cpp:include path="${element.elementPath}" resourceType="${element.elementTypePath}" subtype="edit/tile"/>
</cpp:model>
