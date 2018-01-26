<%@page session="false" pageEncoding="UTF-8"%><%--
--%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%--
--%><cpp:defineObjects/>
<div class="container">
    <div class="row">
        <cpp:element var="site" type="com.composum.pages.commons.model.Site" mode="none"
                     cssClasses="col-md-12">
            <h2>${site.title} <span class="${siteCssBase}_title-type">(Site)</span></h2>
            <ul class="nav nav-tabs" role="tablist">
                <li role="presentation" class="active"><a href="#releases-tab" aria-controls="releases" role="tab"
                                                          data-toggle="tab">Releases</a></li>
                <li role="presentation"><a href="#settings-tab" aria-controls="settings" role="tab"
                                           data-toggle="tab">Settings</a>
                </li>
            </ul>

            <div class="tab-content">
                <div role="tabpanel" class="tab-pane fade in active" id="releases-tab">

                    <sling:include resourceType="composum/pages/stage/edit/site/page/modified"/>

                    <sling:include resourceType="composum/pages/stage/edit/site/page/finished"/>

                    <sling:include path="releases" resourceType="composum/pages/stage/edit/site/releases"/>

                </div>
                <div role="tabpanel" class="tab-pane fade" id="settings-tab">
                    <div class="panel panel-default">
                        <div class="panel-heading" role="tab" id="languagesHead">
                            <h4 class="panel-title">
                                <a role="button" data-toggle="collapse" href="#languagesPanel"
                                   aria-expanded="true" aria-controls="languagesPanel">
                                    Languages
                                </a>
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
                    <div class="panel panel-default">
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
                    </div>
                </div>
            </div>
        </cpp:element>
    </div>
</div>
