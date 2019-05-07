<%@page session="false" pageEncoding="UTF-8"%>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<cpp:defineFrameObjects/>
<cpp:element var="versions" type="com.composum.pages.stage.model.edit.page.Versions" mode="none"
               tagName="none" cssBase="composum-pages-stage-edit-tools-page-versions">
    <ul class="${versionsCssBase}_version-list">
        <c:forEach items="${versions.versionList}" var="version">
            <li class="${versionsCssBase}_version" data-id="${version.id}">
                <div class="${versionsCssBase}_version-head">
                    <span class="${versionsCssBase}_version-name">${version.name}</span>
                    <span class="${versionsCssBase}_version-time">${version.time}</span>
                </div>
                <cpn:text tagName="div" class="${versionsCssBase}_version-labels">${version.labelsString}</cpn:text>
                <div class="${versionsCssBase}_selection">
                    <div class="${versionsCssBase}_selection-main">
                    </div>
                    <div class="${versionsCssBase}_selection-secondary">
                    </div>
                </div>
            </li>
        </c:forEach>
    </ul>
</cpp:element>
