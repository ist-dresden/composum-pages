<%@page session="false" pageEncoding="UTF-8"%><%--
--%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%--
--%><cpp:defineFrameObjects/>
<cpp:element var="assets" type="com.composum.pages.stage.model.edit.FrameModel" mode="none"
             cssClasses="composum-pages-tools">
    <div class="composum-pages-tools_actions btn-toolbar">
        <div class="composum-pages-tools_left-actions">
        </div>
        <div class="composum-pages-tools_right-actions">
            <div class="composum-pages-tools_button-group btn-group btn-group-sm" role="group">
                <button type="button"
                        class="fa fa-filter composum-pages-tools_button btn btn-default dropdown dropdown-toggle"
                        data-toggle="dropdown" title="Filter"><span
                        class="composum-pages-tools_button-label">Filter</span></button>
                <ul class="composum-pages-tools_menu dropdown-menu" role="menu">
                    <li><a href="#" class="${assetsCssBase}_all"
                           title="Show pages only">All</a></li>
                    <li><a href="#" class="${assetsCssBase}_assets"
                           title="How pages and containers">Assets</a></li>
                    <li><a href="#" class="${assetsCssBase}_images"
                           title="Shoe all components">Images</a></li>
                    <li><a href="#" class="${assetsCssBase}_movies"
                           title="Shoe all components">Movies</a></li>
                </ul>
            </div>
            <div class="composum-pages-tools_button-group btn-group btn-group-sm" role="group">
                <button type="button"
                        class="fa fa-search ${assetsCssBase}_search composum-pages-tools_button btn btn-default"
                        title="Search a page"><span
                        class="composum-pages-tools_button-label">Search</span></button>
            </div>
        </div>
    </div>
    <div class="composum-pages-tools_tree-panel">
        <div class="${assetsCssBase}_assets-view">
            <h3>Assets (selector)</h3>
            <p>A list of assets (and variations) which can be inserted in the current page into an appropriate
                container.</p>
            <p>An asset can be inserted dragging the asset dr the variation and dropping it into an element in the main
                page frame.</p>
        </div>
    </div>
</cpp:element>
