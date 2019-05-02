<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineFrameObjects/>
<cpp:element var="versions" type="com.composum.pages.stage.model.edit.page.Versions" mode="none"
             cssClasses="composum-pages-tools composum-pages-tools_context">
    <div class="composum-pages-tools_actions btn-toolbar">
        <div class="composum-pages-tools_left-actions">
            <div class="composum-pages-tools_button-group btn-group btn-group-sm" role="group">
                <button type="button"
                        class="fa fa-desktop ${versionsCssBase}_action_view composum-pages-tools_button btn btn-default"
                        title="Show Version"><span
                        class="composum-pages-tools_button-label">${cpn:i18n(slingRequest,'Show Version')}</span>
                </button>
            </div>
        </div>
        <div class="composum-pages-tools_right-actions">
            <div class="composum-pages-tools_button-group btn-group btn-group-sm" role="group">
                <button type="button"
                        class="fa fa-play ${versionsCssBase}_action_activate composum-pages-tools_button btn btn-default"
                        title="${cpn:i18n(slingRequest,'Activate Page')}"><span
                        class="composum-pages-tools_button-label">${cpn:i18n(slingRequest,'Activate Page')}</span>
                </button>
                <button type="button"
                        class="fa fa-stop ${versionsCssBase}_action_deactivate composum-pages-tools_button btn btn-default"
                        title="${cpn:i18n(slingRequest,'Deactivate Page')}"><span
                        class="composum-pages-tools_button-label">${cpn:i18n(slingRequest,'Deactivate Page')}</span>
                </button>
                <button type="button"
                        class="fa fa-circle-o ${versionsCssBase}_action_checkpoint composum-pages-tools_button btn btn-default"
                        title="${cpn:i18n(slingRequest,'Checkpoint (In/Out) - new version')}"><span
                        class="composum-pages-tools_button-label">${cpn:i18n(slingRequest,'Checkpoint')}</span></button>
            </div>
            <div class="composum-pages-tools_button-group btn-group btn-group-sm" role="group">
                <button type="button"
                        class="fa fa-navicon composum-pages-tools_button btn btn-default dropdown-toggle"
                        data-toggle="dropdown" title="More actions..."><span
                        class="composum-pages-tools_button-label">More...</span></button>
                <ul class="composum-pages-stage-edit-toolbar_more composum-pages-tools_menu dropdown-menu" role="menu">
                    <li><a href="#" class="${versionsCssBase}_action_purge"
                           title="${cpn:i18n(slingRequest,'purge unused versions of the current page')}"><i
                            class="fa fa-trash-o"></i>${cpn:i18n(slingRequest,'Purge Versions (unused)')}</a></li>
                    <li><a href="#" class="${versionsCssBase}_action_check-in"
                           title="${cpn:i18n(slingRequest,'check in the current page')}"><i
                            class="fa fa-sign-in"></i>${cpn:i18n(slingRequest,'Check In')}</a></li>
                    <li><a href="#" class="${versionsCssBase}_action_check-out"
                           title="${cpn:i18n(slingRequest,'check out the current page')}"><i
                            class="fa fa-sign-out"></i>${cpn:i18n(slingRequest,'Check Out')}</a></li>
                    <li><a href="#" class="${versionsCssBase}_action_restore"
                           title="${cpn:i18n(slingRequest,'restore the selected version ot the current page')}"><i
                            class="fa fa-undo"></i>${cpn:i18n(slingRequest,'Restore Version')}</a></li>
                </ul>
            </div>
        </div>
    </div>
    <div class="${versionsCssBase}_panel composum-pages-tools_panel">
        <div class="${versionsCssBase}_versions-head">
            <i class="${versionsCssBase}_selection-icon fa fa-chevron-right"></i>
            <div class="${versionsCssBase}_main-selection">
                <div class="${versionsCssBase}_selection-name"></div>
                <div class="${versionsCssBase}_selection-time"></div>
            </div>
            <div class="${versionsCssBase}_secondary-selection">
                <div class="${versionsCssBase}_selection-name"></div>
                <div class="${versionsCssBase}_selection-time"></div>
            </div>
            <input class="${versionsCssBase}_version-slider widget slider-widget" type="text"
                   data-slider-min="0" data-slider-max="100" data-slider-step="1" data-slider-value="0"/>
        </div>
        <div class="${versionsCssBase}_content">
                <%-- <sling:call script="versionList.jsp"/> - load after init via Ajax --%>
        </div>
    </div>
</cpp:element>
