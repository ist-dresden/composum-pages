<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineObjects/>
<div class="container">
    <div class="row">
        <cpp:element var="site" type="com.composum.pages.commons.model.Site" mode="none"
                     cssAdd="col-md-12">
            <h2>${site.title} <span class="${siteCssBase}_title-type">(Site)</span></h2>
            <ul class="nav nav-tabs" role="tablist">
                <li role="presentation"><a href="#releases-tab" aria-controls="releases" role="tab"
                                           data-toggle="tab">${cpn:i18n(slingRequest,'Releases')}</a></li>
                <li role="presentation" class="active"><a href="#current-tab" aria-controls="current" role="tab"
                                                          title="${cpn:i18n(slingRequest,'pages activated with changes since the previous release')}"
                                                          data-toggle="tab">${cpn:i18n(slingRequest,'Current')}<span
                        class="badge badge-pill changes"><%=site.getReleaseChanges().size()%></span></a></li>
                <li role="presentation"><a href="#modified-tab" aria-controls="modified" role="tab"
                                           title="${cpn:i18n(slingRequest,'modified but last changes not activated yet')}"
                                           data-toggle="tab">${cpn:i18n(slingRequest,'Modified')}<span
                        class="badge badge-pill changes"><%=site.getModifiedPages().size()%></span></a></li>
                <li role="presentation"><a href="#settings-tab" aria-controls="settings" role="tab"
                                           data-toggle="tab">Settings</a></li>
            </ul>

            <div class="tab-content">
                <div role="tabpanel" class="tab-pane fade in" id="releases-tab">
                    <sling:include path="cpl:releases" resourceType="composum/pages/stage/edit/site/releases"/>
                </div>
                <div role="tabpanel" class="tab-pane fade in active" id="current-tab">
                    <sling:include resourceType="composum/pages/stage/edit/site/page/activated"/>
                </div>
                <div role="tabpanel" class="tab-pane fade in" id="modified-tab">
                    <sling:include resourceType="composum/pages/stage/edit/site/page/modified"/>
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
                        <%--div class="panel panel-default">
                            <div class="panel-heading" role="tab" id="templatesHead">
                                <h4 class="panel-title">
                                    <a class="collapsed" role="button" data-toggle="collapse"
                                       href="#templatesPanel" aria-expanded="false" aria-controls="templatesPanel">
                                        Templates
                                    </a>
                                </h4>
                            </div>
                            <div id="templatesPanel" class="panel-collapse collapse" role="tabpanel"
                                 aria-labelledby="templatesHead">
                                <div class="panel-body">

                                </div>
                            </div>
                        </div>
                        <div class="panel panel-default">
                            <div class="panel-heading" role="tab" id="hostsHead">
                                <h4 class="panel-title">
                                    <a class="collapsed" role="button" data-toggle="collapse"
                                       href="#hostsPanel" aria-expanded="false" aria-controls="hostsPanel">
                                        Host Configurations
                                    </a>
                                </h4>
                            </div>
                            <div id="hostsPanel" class="panel-collapse collapse" role="tabpanel"
                                 aria-labelledby="hostsHead">
                                <div class="panel-body">

                                </div>
                            </div>
                        </div--%>
                </div>
            </div>
        </cpp:element>
    </div>
</div>
