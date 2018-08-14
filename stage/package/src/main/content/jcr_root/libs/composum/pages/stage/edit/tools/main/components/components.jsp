<%@page session="false" pageEncoding="UTF-8"%><%--
--%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%--
--%><cpp:defineFrameObjects/>
<cpp:element var="components" type="com.composum.pages.stage.model.edit.page.Components" mode="none"
             cssAdd="composum-pages-tools">
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
                    <li><a href="#" class="${componentsCssBase}_pages"
                           title="Show pages only">Pages</a></li>
                    <li><a href="#" class="${componentsCssBase}_containers"
                           title="How pages and containers">Containers</a></li>
                    <li><a href="#" class="${componentsCssBase}_components"
                           title="Shoe all components">Eelements</a></li>
                </ul>
            </div>
        </div>
    </div>
    <div class="composum-pages-tools_panel">
        <div class="${componentsCssBase}_components-view"></div>
    </div>
</cpp:element>
