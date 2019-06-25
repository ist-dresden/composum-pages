<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineFrameObjects/>
<cpp:model var="site" type="com.composum.pages.commons.model.Site" mode="none">
    <div class="panel panel-default releasesList" data-path="${site.path}">
        <div class="panel-heading" role="tab" id="releasesHead">
            <h4 class="panel-title">
                <a class="collapsed" role="button" data-toggle="collapse"
                   href="#releasesPanel" aria-expanded="false"
                   aria-controls="releasesPanel">${cpn:i18n(slingRequest,'Release List')}</a>
            </h4>
            <c:if test="${site.editMode}">
                <div class="btn-group" role="group" aria-label="...">
                    <button type="button" class="btn btn-danger release-delete"><i
                            class="fa fa-trash"></i>${cpn:i18n(slingRequest,'Delete')}</button>
                </div>
                <div class="btn-group" role="group" aria-label="...">
                    <button type="button" class="btn btn-default release-public"><i
                            class="fa fa-globe"></i>${cpn:i18n(slingRequest,'Public')}</button>
                    <button type="button" class="btn btn-default release-preview"><i
                            class="fa fa-eye"></i>${cpn:i18n(slingRequest,'Preview')}</button>
                </div>
                <div class="btn-group" role="group" aria-label="...">
                    <button type="button" class="btn btn-default release-edit"><i
                            class="fa fa-pencil"></i>${cpn:i18n(slingRequest,'Edit')}</button>
                </div>
            </c:if>
        </div>
        <div id="releasesPanel" class="panel-collapse collapse in" role="tabpanel"
             aria-labelledby="releasesHead">
            <div class="panel-body">
                <table class="${siteCssBase}_table table table-condensed">
                    <tbody class="${siteCssBase}_tbody">
                    <c:forEach items="${site.releases}" var="release">
                        <sling:include resource="${release.resource}"
                                       resourceType="composum/pages/stage/edit/site/releases/release"/>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</cpp:model>
