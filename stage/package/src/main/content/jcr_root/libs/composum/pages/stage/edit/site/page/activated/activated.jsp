<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineFrameObjects/>
<cpp:model var="site" type="com.composum.pages.commons.model.Site" mode="none"
           cssBase="composum-pages-stage-edit-site-page-activated">
    <div class="panel panel-default releaseChanges" data-path="${site.path}">
        <div class="panel-heading" role="tab" id="releaseChangesHead">
            <h4 class="panel-title">
                <a class="collapsed" role="button" data-toggle="collapse"
                   href="#releaseChangesPanel" aria-expanded="false"
                   title="${cpn:i18n(slingRequest,'pages activated with changes since the previous release')}"
                   aria-controls="releaseChangesPanel">${cpn:i18n(slingRequest,'Release Changes')}</a>
            </h4>
            <c:if test="${site.editMode}">
                <div class="btn-group" role="group" aria-label="...">
                    <button class="btn btn-default revert"><i
                            class="fa fa-pause"></i>${cpn:i18n(slingRequest,'Revert')}</button>
                </div>
            </c:if>
        </div>
        <div id="releaseChangesPanel" class="panel-collapse collapse in" role="tabpanel"
             aria-labelledby="releaseChangesHead">
            <div class="${siteCSS}_content panel-body">
                <table class="${siteCSS}_table table table-condensed">
                    <thead class="${siteCSS}_thead">
                    <tr>
                        <th class="_input _page-state"><c:if test="${site.editMode}"><input type="checkbox"
                                                                                            class="${siteCSS}_page-select-all"/></c:if>
                        </th>
                        <th class="_page-path">${cpn:i18n(slingRequest,'Relative Path')}</th>
                        <th class="_page-title">${cpn:i18n(slingRequest,'Title')}</th>
                        <th class="_page-time">${cpn:i18n(slingRequest,'Modification Date')}</th>
                    </tr>
                    </thead>
                    <tbody class="${siteCSS}_tbody">
                    <c:forEach items="${site.releaseChanges}" var="pageVersion">
                        <tr class="release-status_${pageVersion.releaseStatus.activationState}">
                            <td class="_input _page-state"
                                title="${cpn:i18n(slingRequest,pageVersion.releaseStatus.activationState)}"><c:if
                                    test="${site.editMode}"><input type="checkbox"
                                                                   class="${siteCSS}_page-select"
                                                                   data-path="${pageVersion.path}"></c:if>
                            </td>
                            <td class="_page-path"><a
                                    href="${pageVersion.url}" target="_blank">${cpn:path(pageVersion.path)}</a></td>
                            <td class="_page-title">${cpn:text(pageVersion.title)}</td>
                            <td class="_page-time">${cpn:text(pageVersion.lastModifiedString)}</td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</cpp:model>
