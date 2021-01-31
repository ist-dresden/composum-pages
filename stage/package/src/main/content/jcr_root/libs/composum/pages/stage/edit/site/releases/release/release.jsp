<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineFrameObjects/>
<cpp:element var="release" type="com.composum.pages.commons.model.SiteRelease"
             cssBase="composum-pages-site-view_releases_release"
             cssAdd="list-group-item@{release.editMode?' editable':''}">
    <c:if test="${release.editMode}">
        <div class="${releaseCSS}-input"><input type="radio" name="${releaseCSS}_select" value="${release.key}"
                                                data-path="${release.path}" data-label="${release.title}"
                                                class="${releaseCSS}-select${release.public?' is-public':''}${release.preview?' is-preview':''}"/>
        </div>
    </c:if>
    <div class="${releaseCSS}_content">
        <cpp:include replaceSelectors="tile"/>
    </div>
</cpp:element>
