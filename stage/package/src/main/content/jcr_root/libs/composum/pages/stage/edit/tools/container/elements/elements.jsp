<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:container var="container" type="com.composum.pages.stage.model.edit.FrameContainer" mode="none"
               data-pages-edit-name="@{container.name}" data-pages-edit-path="@{container.path}"
               data-pages-edit-type="@{container.type}" cssAdd="composum-pages-tools">
    <div class="composum-pages-tools_actions btn-toolbar">
        <div class="composum-pages-tools_left-actions ${containerCssBase}_actions">
        </div>
        <div class="composum-pages-tools_right-actions">
            <div class="composum-pages-tools_button-group btn-group btn-group-sm" role="group">
                <button type="button"
                        class="fa fa-chevron-up ${containerCssBase}_move-up composum-pages-tools_button btn btn-default"
                        title="${cpn:i18n(slingRequest,'Move Up')}"><span
                        class="composum-pages-tools_button-label">${cpn:i18n(slingRequest,'Move Up')}</span></button>
                <button type="button"
                        class="fa fa-chevron-down ${containerCssBase}_move-down composum-pages-tools_button btn btn-default"
                        title="${cpn:i18n(slingRequest,'Move Down')}"><span
                        class="composum-pages-tools_button-label">${cpn:i18n(slingRequest,'Move Down')}</span></button>
            </div>
            <div class="composum-pages-tools_button-group btn-group btn-group-sm" role="group">
                <button type="button"
                        class="fa fa-eject ${containerCssBase}_go-up composum-pages-tools_button btn btn-default"
                        title="${cpn:i18n(slingRequest,'Go Up')}"><span
                        class="composum-pages-tools_button-label">${cpn:i18n(slingRequest,'Container')}</span></button>
                <button type="button"
                        class="fa fa-bullseye ${containerCssBase}_select composum-pages-tools_button btn btn-default"
                        title="${cpn:i18n(slingRequest,'Select')}"><span
                        class="composum-pages-tools_button-label">${cpn:i18n(slingRequest,'Select Element')}</span>
                </button>
            </div>
        </div>
    </div>
    <div class="composum-pages-tools_panel">
        <div class="${containerCssBase}_elements-view">
            <div class="${containerCssBase}_content">
                    <%-- <cpp:include ... subtype="edit/context/elements"/> - load after init via Ajax --%>
            </div>
        </div>
    </div>
</cpp:container>
