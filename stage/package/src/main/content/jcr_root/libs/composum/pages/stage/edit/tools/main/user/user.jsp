<%@page session="false" pageEncoding="UTF-8"%><%--
--%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%--
--%><cpp:defineFrameObjects/>
<cpp:element var="tree" type="com.composum.pages.stage.model.edit.FrameModel" mode="none"
             cssClasses="composum-pages-tools">
    <div class="composum-pages-tools_actions btn-toolbar">
        <div class="composum-pages-tools_left-actions">
        </div>
        <div class="composum-pages-tools_right-actions">
            <c:if test="${tree.editMode}">
                <div class="composum-pages-tools_button-group btn-group btn-group-sm" role="group">
                    <button type="button"
                            class="fa fa-wrench ${treeCssBase}_develop-mode composum-pages-tools_button btn btn-default"
                            title="Toggle develop mode"><span
                            class="composum-pages-tools_button-label">Develop</span></button>
                </div>
            </c:if>
        </div>
    </div>
    <div class="composum-pages-tools_panel">
        <sling:include resourceType="composum/pages/stage/edit/tools/main/inbox"/>
    </div>
</cpp:element>
