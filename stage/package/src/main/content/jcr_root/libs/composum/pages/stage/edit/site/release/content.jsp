<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineFrameObjects/>
<cpp:model var="model" type="com.composum.pages.stage.model.edit.site.ReleaseModel"
           cssBase="composum-pages-site-view_release_changes">
    <div class="${modelCSS}_details panel panel-default">
        <div class="${modelCSS}_details-head panel-heading"><h4
                class="panel-title">${cpn:i18n(slingRequest,'Release Details')}</h4></div>
        <div class="${modelCSS}_details-body panel-body">
            <cpp:include resourceType="composum/pages/stage/edit/site/releases/release" replaceSelectors="tile"/>
        </div>
    </div>
    <div class="panel panel-default releaseChanges">
        <div class="panel-heading">
            <h4 class="panel-title"
                title="${cpn:i18n(slingRequest,'content activated with changes since the previous release')}">
                    ${cpn:i18n(slingRequest,'Release Changes')}
            </h4>
        </div>
        <div class="${modelCSS}_content panel-body">
            <table class="${modelCSS}_table table table-condensed">
                <thead class="${modelCSS}_thead">
                <tr>
                    <th class="_page-state"></th>
                    <th class="_page-path">${cpn:i18n(slingRequest,'Relative Path')}</th>
                    <th class="_page-title">${cpn:i18n(slingRequest,'Title')}</th>
                    <th class="_page-time">${cpn:i18n(slingRequest,'Modification Date')}</th>
                </tr>
                </thead>
                <tbody class="${modelCSS}_tbody">
                <c:forEach items="${model.release.changes}" var="version">
                    <tr class="release-status_${version.releaseStatus.activationState}">
                        <td class="_page-state"
                            title="${cpn:i18n(slingRequest,version.releaseStatus.activationState)}"></td>
                        <td class="_page-path"><a href="${version.previewUrl}"
                                                  target="_blank">${cpn:path(version.siteRelativePath)}</a>
                        </td>
                        <td class="_page-title">${cpn:text(version.title)}</td>
                        <td class="_page-time">${cpn:text(version.releaseStatus.lastModified)}</td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</cpp:model>
