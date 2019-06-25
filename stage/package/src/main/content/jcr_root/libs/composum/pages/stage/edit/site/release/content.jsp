<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineFrameObjects/>
<cpp:model var="release" type="com.composum.pages.commons.model.SiteRelease"
           cssBase="composum-pages-stage-edit-site-page-activated">
    <div class="panel panel-default releaseChanges">
        <div class="panel-heading">
            <h4 class="panel-title"
                title="${cpn:i18n(slingRequest,'pages activated with changes since the previous release')}">
                    ${cpn:i18n(slingRequest,'Release Changes')}
            </h4>
        </div>
        <div class="${releaseCSS}_content panel-body">
            <table class="${releaseCSS}_table table table-condensed">
                <thead class="${releaseCSS}_thead">
                <tr>
                    <th class="${releaseCSS}_page-path">${cpn:i18n(slingRequest,'relative Path')}</th>
                    <th class="${releaseCSS}_page-title">${cpn:i18n(slingRequest,'Title')}</th>
                    <th class="${releaseCSS}_page-time">${cpn:i18n(slingRequest,'modification Date')}</th>
                </tr>
                </thead>
                <tbody class="${releaseCSS}_tbody">
                <c:forEach items="${release.changes}" var="page">
                    <tr class="release-status_${page.releaseStatus.activationState}">
                        <td class="${releaseCSS}_page-path">${cpn:path(page.siteRelativePath)}</td>
                        <td class="${releaseCSS}_page-title">${cpn:text(page.title)}</td>
                        <td class="${releaseCSS}_page-time">${cpn:text(page.lastModifiedString)}</td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</cpp:model>
