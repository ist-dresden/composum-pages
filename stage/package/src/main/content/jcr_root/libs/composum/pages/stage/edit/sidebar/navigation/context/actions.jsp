<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:model var="site" type="com.composum.pages.stage.model.edit.site.SiteModel" mode="none"
           cssBase="composum-pages-stage-edit-sidebar-navigation-context">
    <div class="${siteCssBase}_actions">
        <div class="composum-pages-tools_button-group btn-group btn-group-sm" role="group">
            <button type="button"
                    class="fa fa-sitemap composum-pages-tools_button btn btn-default restrict-to-site"
                    title="${cpn:i18n(slingRequest,'Restrict to Site subtree')}..."></button>
            <button type="button"
                    class="fa fa-bullseye composum-pages-tools_button btn btn-default goto-site"
                    title="${cpn:i18n(slingRequest,'Go to Site page')}..."></button>
            <button type="button"
                    class="fa fa-database composum-pages-tools_button btn btn-default manage-sites"
                    title="${cpn:i18n(slingRequest,'Manage Sites')}..."></button>
        </div>
    </div>
</cpp:model>
