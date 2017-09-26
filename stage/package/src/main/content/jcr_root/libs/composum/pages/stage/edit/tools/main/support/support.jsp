<%@page session="false" pageEncoding="UTF-8"%><%--
--%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%--
--%><cpp:defineFrameObjects/>
<cpp:element var="support" type="com.composum.pages.stage.model.edit.FrameElement" mode="none"
               cssClasses="composum-pages-tools">
    <div class="composum-pages-tools_actions btn-toolbar">
        <div class="composum-pages-tools_left-actions">
        </div>
        <div class="composum-pages-tools_right-actions">
            <div class="composum-pages-tools_button-group btn-group btn-group-sm" role="group">
            </div>
            <div class="composum-pages-tools_button-group btn-group btn-group-sm" role="group">
                <button type="button"
                        class="fa fa-search ${supportCssBase}_search composum-pages-tools_button btn btn-default"
                        title="Search a page"><span
                        class="composum-pages-tools_button-label">Search</span></button>
            </div>
        </div>
    </div>
    <div class="composum-pages-tools_panel">
        <div class="${supportCssBase}_support-view">
            <h3>Editing Hints</h3>
            <p>The components support view.</p>
            <p>Each component should have a content resource which describes the component of the currently selected element and helps to use the component.</p>
        </div>
    </div>
</cpp:element>
