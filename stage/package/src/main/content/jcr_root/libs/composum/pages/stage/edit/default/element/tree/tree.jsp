<%@page session="false" pageEncoding="utf-8" %><%--
--%><%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><cpp:defineFrameObjects/>
<cpp:editToolbar var="frame" type="com.composum.pages.stage.model.edit.FramePage">
    <div class="composum-pages-tools_button-group btn-group btn-group-sm" role="group">
        <button type="button"
                class="fa fa-edit ${treeCssBase}_edit composum-pages-tools_button btn btn-default"
                title="${cpn:i18n(slingRequest,'Edit the selected element')}" data-action="window.composum.pages.actions.element.edit"><span
                class="composum-pages-tools_button-label">${cpn:i18n(slingRequest,'Edit')}</span></button>
    </div>
    <div class="composum-pages-tools_button-group btn-group btn-group-sm" role="group">
        <button type="button"
                class="fa fa-copy ${treeCssBase}_create composum-pages-tools_button btn btn-default"
                title="${cpn:i18n(slingRequest,'Copy the selected element')}" data-action="window.composum.pages.actions.element.copy"><span
                class="composum-pages-tools_button-label">${cpn:i18n(slingRequest,'Copy')}</span></button>
        <button type="button"
                class="fa fa-trash ${treeCssBase}_delete composum-pages-tools_button btn btn-default"
                title="${cpn:i18n(slingRequest,'Delete the selected element')}" data-action="window.composum.pages.actions.element.delete"><span
                class="composum-pages-tools_button-label">${cpn:i18n(slingRequest,'Delete')}</span></button>
    </div>
</cpp:editToolbar>
