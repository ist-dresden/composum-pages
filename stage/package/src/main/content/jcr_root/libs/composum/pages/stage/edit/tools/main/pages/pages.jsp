<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineFrameObjects/>
<cpp:element var="tree" type="com.composum.pages.stage.model.edit.FramePage" mode="none"
             cssClasses="composum-pages-tools">
    <c:if test="${tree.editMode}">
        <div class="composum-pages-tools_actions btn-toolbar">
            <div class="composum-pages-tools_left-actions">
            </div>
            <div class="composum-pages-tools_right-actions">
                <div class="composum-pages-tools_button-group btn-group btn-group-sm" role="group">
                    <button type="button"
                            class="fa fa-filter composum-pages-tools_button btn btn-default dropdown dropdown-toggle"
                            data-toggle="dropdown" title="Filter"><span
                            class="composum-pages-tools_button-label">Filter</span></button>
                    <ul class="${treeCssBase}_filter composum-pages-tools_menu dropdown-menu" role="menu">
                        <li class="${treeCssBase}_filter-value"
                            data-value="page"><a href="#" title="Show pages only">Page</a>
                        </li>
                        <li class="${treeCssBase}_filter-value"
                            data-value="container"><a href="#" title="Show pages and containers">Container</a>
                        </li>
                        <li class="${treeCssBase}_filter-value"
                            data-value="element"><a href="#" title="Show all elements">Element</a>
                        </li>
                    </ul>
                </div>
                <div class="composum-pages-tools_button-group btn-group btn-group-sm" role="group">
                    <button type="button"
                            class="fa fa-search ${treeCssBase}_toggle-view composum-pages-tools_button btn btn-default"
                            title="${cpn:i18n(slingRequest,'Search a page')}"><cpn:text
                            tagName="span" tagClass="composum-pages-tools_button-label"
                            i18n="true">Search</cpn:text></button>
                </div>
            </div>
        </div>
    </c:if>
    <%-- the '.tree-panel' around the tree is important for the 'auto-scroll to selected node' tree feature --%>
    <div class="composum-pages-tools_tree-panel tree-panel">
        <div class="composum-pages-tools_tree">
        </div>
    </div>
    <cpp:include resourceType="composum/pages/stage/edit/tools/search" replaceSelectors="page"/>
</cpp:element>
