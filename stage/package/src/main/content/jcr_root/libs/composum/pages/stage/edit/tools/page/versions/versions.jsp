<%@page session="false" pageEncoding="UTF-8"%><%--
--%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%--
--%><cpp:defineFrameObjects/>
<cpp:element var="versions" type="com.composum.pages.stage.model.edit.page.Versions" mode="none"
               cssClasses="composum-pages-tools composum-pages-tools_context">
    <div class="composum-pages-tools_actions btn-toolbar">
        <div class="composum-pages-tools_left-actions">
            <div class="composum-pages-tools_button-group btn-group btn-group-sm" role="group">
                <button type="button"
                        class="fa fa-desktop ${versionsCssBase}_action_view composum-pages-tools_button btn btn-default"
                        title="View Version"><span
                        class="composum-pages-tools_button-label">View</span></button>
            </div>
        </div>
        <div class="composum-pages-tools_right-actions">
            <div class="composum-pages-tools_button-group btn-group btn-group-sm" role="group">
                <button type="button"
                        class="fa fa-circle-o ${versionsCssBase}_action_checkpoint composum-pages-tools_button btn btn-default"
                        title="Checkpoint (In/Out)"><span
                        class="composum-pages-tools_button-label">Checkpoint</span></button>
                <button type="button"
                        class="fa fa-sign-in ${versionsCssBase}_action_check-in composum-pages-tools_button btn btn-default"
                        title="Check in the selected page"><span
                        class="composum-pages-tools_button-label">Check In</span></button>
                <button type="button"
                        class="fa fa-sign-out ${versionsCssBase}_action_check-out composum-pages-tools_button btn btn-default"
                        title="Check out the selected page"><span
                        class="composum-pages-tools_button-label">Check Out</span></button>
                <button type="button"
                        class="fa fa-undo ${versionsCssBase}_action_restore composum-pages-tools_button btn btn-default"
                        title="Restore the selected version"><span
                        class="composum-pages-tools_button-label">Restore</span></button>
            </div>
            <div class="composum-pages-tools_button-group btn-group btn-group-sm" role="group">
                <button type="button"
                        class="fa fa-navicon composum-pages-tools_button btn btn-default dropdown-toggle"
                        data-toggle="dropdown" title="More actions..."><span
                        class="composum-pages-tools_button-label">More...</span></button>
                <ul class="composum-pages-tools_menu dropdown-menu" role="menu">
                    <!--
                    <li><a href="#" class="${versionsCssBase}_action_delete"
                           title="Delete Version">Delete Version</a></li>
                    <li><a href="#" class="${versionsCssBase}_action_add-label"
                           title="Add version label">Add Label</a></li>
                    <li><a href="#" class="${versionsCssBase}_action_remove-label"
                           title="Remove version label">Remove Label</a></li>
                    -->
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
