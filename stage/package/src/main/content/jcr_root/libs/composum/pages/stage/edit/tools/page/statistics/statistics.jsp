<%@page session="false" pageEncoding="UTF-8"%><%--
--%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%--
--%><cpp:defineFrameObjects/>
<cpp:element var="statistics" type="com.composum.pages.stage.model.edit.FramePage" mode="none"
               cssClasses="composum-pages-tools">
    <div class="composum-pages-tools_actions btn-toolbar">
        <div class="composum-pages-tools_left-actions">
        </div>
        <div class="composum-pages-tools_right-actions">
            <div class="composum-pages-tools_button-group btn-group btn-group-sm" role="group">
            </div>
            <div class="composum-pages-tools_button-group btn-group btn-group-sm" role="group">
                <button type="button"
                        class="fa fa-search ${statisticsCssBase}_search composum-pages-tools_button btn btn-default"
                        title="Search a page"><span
                        class="composum-pages-tools_button-label">Search</span></button>
            </div>
        </div>
    </div>
    <div class="composum-pages-tools_panel">
        <div class="${statisticsCssBase}_statistics-view">
            <h3>Statistics</h3>
            <p>The page statistics view.</p>
            <p>A simple mechanism to track page views should be part of the basic platform; the collected data will be visualized here.</p>
        </div>
    </div>
</cpp:element>
