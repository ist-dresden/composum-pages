<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog var="site" type="com.composum.pages.commons.model.Site" selector="generic" languageContext="false"
                title="Finalize Release" submitLabel="Finalize" submit="/bin/cpm/pages/release.finalize.json"
                successEvent="site:changed">
    <input name="path" type="hidden" value="${site.path}" class="${siteCSS}_path"/>
    <div class="row" style="align-items:flex-start;">
        <div class="col col-xs-8">
            <div class="row">
                <div class="col col-xs-6">
                    <cpp:widget name="jcr:title" label="Title" type="textfield"
                                value="${site.currentRelease.title}"/>
                </div>
                <div class="col col-xs-6">
                    <label class="composum-pages-edit-widget_label control-label">${cpn:i18n(slingRequest,'Number')}</label>
                    <div class="${siteCSS}_number widget composum-pages-edit-widget_radio-group">
                        <div class="radio">
                            <label><input type="radio" name="number" value="MAJOR"><span
                                    class="value">${site.currentRelease.nextReleaseNumbers.MAJOR}</span><span
                                    class="hint">(${cpn:i18n(slingRequest,'major / essential')})</span></label>
                        </div>
                        <div class="radio">
                            <label><input type="radio" name="number" value="MINOR"><span
                                    class="value">${site.currentRelease.nextReleaseNumbers.MINOR}</span><span
                                    class="hint">(${cpn:i18n(slingRequest,'minor / modification')})</span></label>
                        </div>
                        <div class="radio">
                            <label><input type="radio" name="number" value="BUGFIX"><span
                                    class="value">${site.currentRelease.nextReleaseNumbers.BUGFIX}</span><span
                                    class="hint">(${cpn:i18n(slingRequest,'bugfix / correction')})</span></label>
                        </div>
                    </div>
                </div>
            </div>
            <cpp:widget name="jcr:description" label="Description" type="richtext" height="170"
                        value="${site.currentRelease.description}"/>
        </div>
        <div class="col col-xs-4">
            <cpp:widget type="static" i18n="true" level="remark"
                        value="The release number is built by incrementing the number of the last release at the segment you've chosen."/>
            <cpp:widget type="static" i18n="true" level="remark"
                        value="If the finalized release should be published imediately select the designated stages..."/>
            <div class="form-group">
                <label class="composum-pages-edit-widget_label control-label">${cpn:i18n(slingRequest,'Publish')}</label>
                <div class="${siteCSS}_publish composum-pages-edit-widget_category-set">
                    <label class="checkbox label label-primary"><input
                            type="checkbox" name="publish" value="public"${site.publishSuggestion[0]?' checked':''}/>public</label>
                    <label class="checkbox label label-primary"><input
                            type="checkbox" name="publish" value="preview"${site.publishSuggestion[1]?' checked':''}/>preview</label>
                </div>
            </div>
            <cpp:widget type="static" i18n="true" level="remark"
                        value="If the next open release should be published imediately select its designated stages also..."/>
            <div class="form-group">
                <label class="composum-pages-edit-widget_label control-label">${cpn:i18n(slingRequest,"'current' Release")}</label>
                <div class="${siteCSS}_current composum-pages-edit-widget_category-set">
                    <label class="checkbox label label-primary"><input
                            type="checkbox" name="current" value="public"${site.publishSuggestion[2]?' checked':''}/>public</label>
                    <label class="checkbox label label-primary"><input
                            type="checkbox" name="current" value="preview"${site.publishSuggestion[3]?' checked':''}/>preview</label>
                </div>
            </div>
        </div>
    </div>
</cpp:editDialog>
