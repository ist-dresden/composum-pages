<%@page session="false" pageEncoding="utf-8" %><%--
--%><%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><cpp:defineFrameObjects/>
<cpp:editToolbar var="frame" type="com.composum.pages.stage.model.edit.FramePage">
    <div class="composum-pages-tools_button-group btn-group btn-group-sm" role="group">
        <button type="button"
                class="fa fa-edit ${treeCssBase}_create composum-pages-tools_button btn btn-default"
                title="${cpn:i18n(slingRequest,'Rename the folder')}" data-action="window.composum.pages.actions.folder.reename"><span
                class="composum-pages-tools_button-label">${cpn:i18n(slingRequest,'Rename')}</span></button>
        <button type="button"
                class="fa fa-arrows-alt ${treeCssBase}_create composum-pages-tools_button btn btn-default"
                title="${cpn:i18n(slingRequest,'Move the folder')}" data-action="window.composum.pages.actions.folder.move"><span
                class="composum-pages-tools_button-label">${cpn:i18n(slingRequest,'Move')}</span></button>
    </div>
    <div class="composum-pages-tools_button-group btn-group btn-group-sm" role="group">
        <button type="button"
                class="fa fa-plus ${treeCssBase}_create composum-pages-tools_button btn btn-default"
                title="${cpn:i18n(slingRequest,'Create a new folder')}" data-action="window.composum.pages.actions.folder.create"><span
                class="composum-pages-tools_button-label">${cpn:i18n(slingRequest,'Create')}</span></button>
        <button type="button"
                class="fa fa-copy ${treeCssBase}_create composum-pages-tools_button btn btn-default"
                title="${cpn:i18n(slingRequest,'Copy the selected folder')}" data-action="window.composum.pages.actions.folder.copy"><span
                class="composum-pages-tools_button-label">${cpn:i18n(slingRequest,'Copy')}</span></button>
        <button type="button"
                class="fa fa-paste ${treeCssBase}_create composum-pages-tools_button btn btn-default"
                title="${cpn:i18n(slingRequest,'Paste element as child of the selected folder')}" data-action="window.composum.pages.actions.folder.paste"><span
                class="composum-pages-tools_button-label">${cpn:i18n(slingRequest,'Paste')}</span></button>
        <button type="button"
                class="fa fa-trash ${treeCssBase}_delete composum-pages-tools_button btn btn-default"
                title="${cpn:i18n(slingRequest,'Delete the selected folder')}" data-action="window.composum.pages.actions.folder.delete"><span
                class="composum-pages-tools_button-label">${cpn:i18n(slingRequest,'Delete')}</span></button>
    </div>
</cpp:editToolbar>
