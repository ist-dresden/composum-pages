<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<sling:defineObjects/>
<cpn:component var="model" type="com.composum.pages.commons.model.SiteRelease">
    <div class="composum-pages-site-view_releases_publish-target panel panel-info">
        <div class="composum-pages-site-view_releases_release-heading panel-heading">
            <cpn:text class="composum-pages-site-view_releases_release-title">${model.titleString}</cpn:text>
            <cpn:text class="composum-pages-site-view_releases_release-key">${model.key}</cpn:text>
            <cpn:text test="${not model.current}" class="composum-pages-site-view_releases_release-creationDate date"
                      format="created: {}" i18n="true" value="${model.creationDateString}"/>
        </div>
        <cpn:text class="panel-body composum-pages-site-view_releases_release-description"
                  type="rich">${model.description}</cpn:text>
    </div>
</cpn:component>
