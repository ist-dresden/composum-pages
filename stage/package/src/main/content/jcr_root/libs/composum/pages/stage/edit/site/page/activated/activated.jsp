<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineFrameObjects/>
<cpp:model var="site" type="com.composum.pages.commons.model.Site" mode="none"
           cssBase="composum-pages-stage-edit-site-page-activated">
    <div class="panel panel-default activatedPages" data-path="${site.path}">
        <div class="panel-heading" role="tab" id="activatedPagesHead">
            <h4 class="panel-title">
                <a class="collapsed" role="button" data-toggle="collapse"
                   href="#activatedPagesPanel" aria-expanded="false"
                   aria-controls="activatedPagesPanel">${cpn:i18n(slingRequest,'Changed Pages')}</a>
            </h4>
            <c:if test="${site.editMode}">
                <div class="btn-group" role="group" aria-label="...">
                    <button class="btn btn-default release"><i
                            class="fa fa-pause"></i>${cpn:i18n(slingRequest,'Revert')}</button>
                </div>
                <%--label class="filter-input heading-input"><input type="checkbox" class="filter-changed checkbox"
                                                                 checked="checked"/>${cpn:i18n(slingRequest,'changes only')}
                </label--%>
            </c:if>
        </div>
        <div id="activatedPagesPanel" class="panel-collapse collapse in" role="tabpanel"
             aria-labelledby="activatedPagesHead">
            <div class="panel-body">
                <table class="${siteCssBase}_table table table-condensed">
                    <thead class="${siteCssBase}_thead">
                    <tr>
                        <c:if test="${site.editMode}">
                            <th class="_input"><input type="checkbox" class="${siteCssBase}_page-select-all"/></th>
                        </c:if>
                        <th class="${siteCssBase}_page-path">${cpn:i18n(slingRequest,'rel. Path')}</th>
                        <th class="${siteCssBase}_page-title">${cpn:i18n(slingRequest,'Title')}</th>
                        <th class="${siteCssBase}_page-time">${cpn:i18n(slingRequest,'modification Date')}</th>
                    </tr>
                    </thead>
                    <tbody class="${siteCssBase}_tbody">
                    <c:forEach items="${site.unreleasedPages}" var="page">
                        <tr class="release-status_${page.releaseStatus.activationState}">
                            <c:if test="${site.editMode}">
                                <td class="_input"><input type="checkbox" class="${siteCssBase}_page-select"
                                                          data-path="${page.path}"></td>
                            </c:if>
                            <td class="${siteCssBase}_page-path">${cpn:path(page.siteRelativePath)}</td>
                            <td class="${siteCssBase}_page-title">${cpn:text(page.title)}</td>
                            <td class="${siteCssBase}_page-time">${cpn:text(page.lastModifiedString)}</td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</cpp:model>
