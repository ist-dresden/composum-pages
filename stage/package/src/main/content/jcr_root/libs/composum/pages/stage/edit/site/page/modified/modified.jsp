<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineFrameObjects/>
<cpp:model var="model" type="com.composum.pages.stage.model.edit.site.SiteModel" mode="none"
           cssBase="composum-pages-stage-edit-site-page-modified">
    <div class="panel panel-default modifiedContent" data-path="${model.site.path}">
        <div class="panel-heading" role="tab" id="modifiedContentHead">
            <h4 class="panel-title">
                <a class="collapsed" role="button" data-toggle="collapse"
                   href="#modifiedContentPanel" aria-expanded="false"
                   title="${cpn:i18n(slingRequest,'modified but last changes not activated yet')}"
                   aria-controls="modifiedContentPanel">${cpn:i18n(slingRequest,'Modified')}</a>
                <cpp:include resourceType="composum/pages/stage/edit/site/page/type"/>
            </h4>
            <div class="btn-group" role="group">
                <cpp:include resourceType="composum/pages/stage/edit/site/page/filter" replaceSelectors="modified"/>
                <c:if test="${model.editMode}">
                    <button class="btn btn-default activate"><i
                            class="fa fa-play"></i>${cpn:i18n(slingRequest,'Activate')}</button>
                </c:if>
            </div>
        </div>
        <div id="modifiedContentPanel" class="panel-collapse collapse in" role="tabpanel"
             aria-labelledby="modifiedContentHead">
            <div class="${modelCSS}_content panel-body">
                <table class="${modelCSS}_table table table-condensed">
                    <thead class="${modelCSS}_thead">
                    <tr>
                        <th class="_input _page-state"><c:if test="${model.editMode}"><input type="checkbox"
                                                                                             class="${modelCSS}_page-select-all"/></c:if>
                        </th>
                        <th class="_page-path">${cpn:i18n(slingRequest,'Relative Path')}</th>
                        <th class="_page-title">${cpn:i18n(slingRequest,'Title')}</th>
                        <th class="_page-time">${cpn:i18n(slingRequest,'Modification Date')}</th>
                    </tr>
                    </thead>
                    <tbody class="${modelCSS}_tbody">
                    <c:forEach items="${model.modifiedContent}" var="version">
                        <tr class="release-status_${version.releaseStatus.activationState}">
                            <td class="_input _page-state"
                                title="${cpn:i18n(slingRequest,version.releaseStatus.activationState)}"><c:if
                                    test="${model.editMode}"><input type="checkbox"
                                                                    class="${modelCSS}_page-select"
                                                                    data-path="${version.path}"></c:if>
                            </td>
                            <td class="_page-path">
                                <c:choose>
                                    <c:when test="${not empty version.previewUrl}">
                                        <a href="${version.previewUrl}">${cpn:path(version.siteRelativePath)}</a>
                                    </c:when>
                                    <c:otherwise>
                                        ${cpn:path(version.siteRelativePath)}
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td class="_page-title">${cpn:text(version.title)}</td>
                            <td class="_page-time">${cpn:text(version.lastModifiedString)}</td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</cpp:model>
