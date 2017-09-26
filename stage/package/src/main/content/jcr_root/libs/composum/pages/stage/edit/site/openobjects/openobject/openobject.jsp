<%@page session="false" pageEncoding="UTF-8"%><%--
--%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><cpp:defineFrameObjects/>
<cpp:element var="openObject" type="com.composum.pages.commons.model.Page" tagName="tr">
    <td class="${openObjectCssBase}_title">${openObject.siteRelativePath}</td>
    <td class="${openObjectCssBase}_title">${openObject.title}</td>
    <td class="${openObjectCssBase}_title">${openObject.lastModifiedString}</td>
</cpp:element>
