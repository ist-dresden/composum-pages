<%@page session="false" pageEncoding="utf-8" %><%--
--%><%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><cpp:defineFrameObjects/>
<cpp:editToolbar var="frame" type="com.composum.pages.stage.model.edit.FramePage">
    <div class="composum-pages-tools_button-group btn-group btn-group-sm" role="group">
        <button type="button"
                class="fa fa-edit ${treeCssBase}_create composum-pages-tools_button btn btn-default"
                title="${cpn:i18n(slingRequest,'Edit the site prroperties')}" data-action="window.composum.pages.actions.site.edit"><span
                class="composum-pages-tools_button-label">${cpn:i18n(slingRequest,'Create')}</span></button>
    </div>
    <div class="composum-pages-tools_button-group btn-group btn-group-sm" role="group">
        <button type="button"
                class="fa fa-plus ${treeCssBase}_create composum-pages-tools_button btn btn-default"
                title="${cpn:i18n(slingRequest,'Create a new site')}" data-action="window.composum.pages.actions.site.create"><span
                class="composum-pages-tools_button-label">${cpn:i18n(slingRequest,'Create')}</span></button>
        <!--
        <button type="button"
                class="fa fa-arrows-alt ${treeCssBase}_create composum-pages-tools_button btn btn-default"
                title="${cpn:i18n(slingRequest,'Move the selected site')}" data-action="window.composum.pages.actions.site.move"><span
                class="composum-pages-tools_button-label">${cpn:i18n(slingRequest,'Create')}</span></button>
        -->
        <button type="button"
                class="fa fa-trash ${treeCssBase}_delete composum-pages-tools_button btn btn-default"
                title="${cpn:i18n(slingRequest,'Delete the selected site')}" data-action="window.composum.pages.actions.site.delete"><span
                class="composum-pages-tools_button-label">${cpn:i18n(slingRequest,'Delete')}</span></button>
    </div>
</cpp:editToolbar>
