<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineFrameObjects/>
<cpp:element var="release" type="com.composum.pages.commons.model.SiteRelease" tagName="tr">
    <c:if test="${release.editMode}">
        <td class="${releaseCSS}_input"><input type="radio" class="${releaseCSS}_select"
                                               name="${releaseCSS}_select"
                                               value="${release.key}" data-path="${release.path}"/></td>
    </c:if>
    <td class="${releaseCSS}_content">
        <cpp:include replaceSelectors="tile"/>
    </td>
</cpp:element>
