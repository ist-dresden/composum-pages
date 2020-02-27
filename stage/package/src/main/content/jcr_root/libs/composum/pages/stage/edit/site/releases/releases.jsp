<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineFrameObjects/>
<cpp:model var="site" type="com.composum.pages.commons.model.Site" mode="none"
           cssBase="composum-pages-site-view">
    <div class="${siteCSS}_releases panel panel-default releasesList" data-path="${site.path}">
        <div class="${siteCSS}_heading panel-heading" role="tab" id="releasesHead">
            <h4 class="${siteCSS}_title panel-title">${cpn:i18n(slingRequest,'Release List')}</h4>
            <c:if test="${site.editMode}">
                <div role="group" class="${siteCSS}_btn-group btn-group">
                    <button type="button"
                            class="${siteCSS}_releases_edit btn btn-default release-edit"
                            title="${cpn:i18n(slingRequest,'Edit Release properties')}..."><i
                            class="fa fa-pencil"></i>${cpn:i18n(slingRequest,'Edit')}</button>
                </div>
                <div role="group" class="${siteCSS}_btn-group btn-group">
                    <button type="button"
                            class="${siteCSS}_releases_public btn btn-default release-public"
                            title="${cpn:i18n(slingRequest,'Switch Public Release to the selected release (publish)')}...">
                        <i class="fa fa-globe"></i>${cpn:i18n(slingRequest,'Public')}</button>
                    <button type="button"
                            class="${siteCSS}_releases_preview btn btn-default release-preview"
                            title="${cpn:i18n(slingRequest,'Switch Preview Release to the selected release')}..."><i
                            class="fa fa-eye"></i>${cpn:i18n(slingRequest,'Preview')}</button>
                </div>
                <div role="group" class="${siteCSS}_btn-group btn-group">
                    <button type="button"
                            class="${siteCSS}_releases_delete btn btn-danger release-delete"
                            title="${cpn:i18n(slingRequest,'Delete the selected release')}...">
                        <i class="fa fa-trash"></i>${cpn:i18n(slingRequest,'Delete')}</button>
                </div>
            </c:if>
        </div>
        <div class="${siteCSS}_content list-group">
            <c:forEach items="${site.releases}" var="release">
                <sling:include resource="${release.resource}"
                               resourceType="composum/pages/stage/edit/site/releases/release"/>
            </c:forEach>
        </div>
    </div>
</cpp:model>
