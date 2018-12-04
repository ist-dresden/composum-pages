<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<div class="composum-pages-stage-edit-sidebar-navigation-context_actions">
    <div class="composum-pages-tools_button-group btn-group btn-group-sm" role="group">
        <button type="button"
                class="fa fa-database composum-pages-tools_button btn btn-default manage-sites"
                title="${cpn:i18n(slingRequest,'Manage Sites')}..."></button>
    </div>
</div>
<div class="composum-pages-stage-edit-sidebar-navigation-context_no-site">
    <div class="composum-pages-stage-site_tile">
        <div class="composum-pages-stage-site_thumbnail">
            <div class="composum-pages-stage-site_thumbnail_wrapper">
                <picture class="composum-pages-stage-site_thumbnail_picture">
                    <div class="composum-pages-stage-site_thumbnail_image composum-pages-stage-site_thumbnail_placeholder fa fa-question"></div>
                </picture>
            </div>
        </div>
        <div class="composum-pages-stage-site_tile_text">
            <h3 class="composum-pages-stage-site_tile_title manage-sites">${cpn:i18n(slingRequest,'Select or create a Site')}...</h3>
        </div>
    </div>
</div>
