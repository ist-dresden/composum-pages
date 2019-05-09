<%@page session="false" pageEncoding="UTF-8"%><%--
--%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%--
--%><cpp:defineFrameObjects/>
<cpp:element var="release" type="com.composum.pages.commons.model.SiteRelease"
             tagName="tr">
    <c:if test="${release.editMode}">
        <td><input type="radio" class="${releaseCssBase}_select" name="${releaseCssBase}_select"
                   value="${release.key}" data-path="${release.path}"/></td>
    </c:if>
    <td class="${releaseCssBase}_key">${release.key}</td>
    <td class="${releaseCssBase}_title">${release.title}</td>
    <td class="${releaseCssBase}_description">${release.description}</td>
    <td class="${releaseCssBase}_categories">
        <c:forEach items="${release.categories}" var="category">
            <span class="label label-primary">${category}</span>
        </c:forEach>
    </td>
</cpp:element>
