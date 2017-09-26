<%@page session="false" pageEncoding="UTF-8"%><%--
--%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%--
--%><cpp:defineFrameObjects/>
<cpp:element var="context" type="com.composum.pages.stage.model.edit.FramePage" mode="none"
               cssClasses="composum-pages-tools">
    <div class="composum-pages-tools_actions btn-toolbar">
        <div class="composum-pages-tools_left-actions">
            <div class="composum-pages-tools_button-group btn-group btn-group-sm" role="group">
                <button type="button"
                        class="fa fa-edit ${contextCssBase}_button-edit composum-pages-tools_button btn btn-default"
                        title="Edit Element Properties"><span
                        class="composum-pages-tools_button-label">Edit Properties</span></button>
            </div>
        </div>
        <div class="composum-pages-tools_right-actions">
    </div>
    </div>
    <div class="composum-pages-tools_panel">
        <div class="${contextCssBase}_element-context">
        </div>
    </div>
</cpp:element>
