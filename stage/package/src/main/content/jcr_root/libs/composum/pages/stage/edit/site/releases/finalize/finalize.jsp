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
            <cpp:widget name="jcr:title" label="Title" type="textfield"
                        value="${site.currentRelease.properties.title}"/>
            <cpp:widget name="jcr:description" label="Description" type="richtext"
                        value="${site.currentRelease.description}"/>
        </div>
        <div class="col col-xs-4">
            <label class="composum-pages-edit-widget_label control-label">${cpn:i18n(slingRequest,'Number')}</label>
            <div class="composum-pages-edit-widget_radio-group">
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
            <cpp:widget type="static" i18n="true" level="remark"
                        value="The release number is built incrementing the number of the last release at the segment you've chosen."/>
            <cpp:widget type="static" i18n="true" level="remark"
                        value="If the new release should be published imediately select the designated stages..."/>
            <div class="form-group">
                <label class="composum-pages-edit-widget_label control-label">${cpn:i18n(slingRequest,'Publish')}</label>
                <div class="composum-pages-edit-widget_category-set">
                    <label class="checkbox label label-primary"><input type="checkbox" name="publish"
                                                                       value="public"/>public</label>
                    <label class="checkbox label label-primary"><input type="checkbox" name="publish"
                                                                       value="preview"/>preview</label>
                </div>
            </div>
        </div>
    </div>
</cpp:editDialog>
