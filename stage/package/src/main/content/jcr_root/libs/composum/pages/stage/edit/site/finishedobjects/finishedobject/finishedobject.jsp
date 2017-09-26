<%@page session="false" pageEncoding="UTF-8"%><%--
--%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><cpp:defineFrameObjects/>
<cpp:element var="finished" type="com.composum.pages.commons.model.Page" tagName="tr">
    <td class="${finishedCssBase}_select"><input type="checkbox" class="${finishedCssBase}_select"
                                                 name="${finishedCssBase}_select"
                                                 data-path="${finished.path}"></td>
    <td class="${finished}_title">${finished.siteRelativePath}</td>
    <td class="${finished}_title">${finished.title}</td>
    <td class="${finished}_title">${finished.lastModifiedString}</td>
</cpp:element>
