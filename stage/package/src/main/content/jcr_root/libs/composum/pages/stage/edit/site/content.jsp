<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineObjects/>
<cpp:model var="site" type="com.composum.pages.commons.model.Site">
    <ul class="nav nav-tabs" role="tablist">
        <li role="presentation"><a href="#releases-tab" aria-controls="releases-tab" role="tab"
                                   data-toggle="tab">${cpn:i18n(slingRequest,'Releases')}</a></li>
        <li role="presentation"><a href="#current-tab" aria-controls="current-tab" role="tab"
                                   title="${cpn:i18n(slingRequest,'content activated with changes since the previous release')}"
                                   data-toggle="tab">${cpn:i18n(slingRequest,'Activated')}<span
                class="badge badge-pill changes"><%=site.getReleaseChanges().size()%></span></a></li>
        <li role="presentation"><a href="#modified-tab" aria-controls="modified-tab" role="tab"
                                   title="${cpn:i18n(slingRequest,'modified but last changes not activated yet')}"
                                   data-toggle="tab">${cpn:i18n(slingRequest,'Modified')}<span
                class="badge badge-pill changes"><%=site.getModifiedContent().size()%></span></a></li>
        <li role="presentation"><a href="#replication-tab" aria-controls="replication-tab" role="tab" data-toggle="tab"
                                   class="composum-pages-stage-edit-site_tab-replication">${cpn:i18n(slingRequest,'Replication')}&nbsp;
            <sling:include resourceType="composum/platform/services/replication/status"
                           replaceSelectors="badge.public"/>
            <sling:include resourceType="composum/platform/services/replication/status"
                           replaceSelectors="badge.preview"/></a>
        </li>
        <li role="presentation"><a href="#settings-tab" aria-controls="settings-tab" role="tab"
                                   data-toggle="tab">${cpn:i18n(slingRequest,'Settings')}</a></li>
    </ul>

    <div class="tab-content">
        <div role="tabpanel" class="tab-pane fade in" id="releases-tab">
            <sling:include path="cpl:releases" resourceType="composum/pages/stage/edit/site/releases"/>
        </div>
        <div role="tabpanel" class="tab-pane fade in" id="current-tab">
            <sling:include resourceType="composum/pages/stage/edit/site/page/activated"/>
        </div>
        <div role="tabpanel" class="tab-pane fade in" id="modified-tab">
            <sling:include resourceType="composum/pages/stage/edit/site/page/modified"/>
        </div>
        <div role="tabpanel" class="tab-pane fade in" id="replication-tab">
            <sling:include resourceType="composum/platform/services/replication/status" replaceSelectors="public"/>
            <sling:include resourceType="composum/platform/services/replication/status" replaceSelectors="preview"/>
        </div>
        <div role="tabpanel" class="tab-pane fade in" id="settings-tab">
            <div class="panel panel-default">
                <div class="panel-heading" role="tab" id="languagesHead">
                    <h4 class="panel-title"><a role="button" data-toggle="collapse" href="#languagesPanel"
                                               aria-expanded="true"
                                               aria-controls="languagesPanel">${cpn:i18n(slingRequest,'Languages')}</a>
                    </h4>
                    <div class="btn-group" role="group" aria-label="edit languages">
                        <cpp:invoke action="openEditDialog"
                                    resourceType="composum/pages/stage/edit/site/languages"
                                    tagName="button" cssAdd="btn btn-default fa fa-pencil"/>
                    </div>
                </div>
                <div id="languagesPanel" class="panel-collapse collapse in" role="tabpanel"
                     aria-labelledby="languagesHead">
                    <div class="panel-body">
                        <sling:include path="languages"
                                       resourceType="composum/pages/stage/edit/site/languages"/>
                    </div>
                </div>
            </div>
        </div>
    </div>
</cpp:model>
