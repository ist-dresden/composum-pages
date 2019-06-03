<%@page session="false" pageEncoding="UTF-8"%><%--
--%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%--
--%><cpp:defineFrameObjects/>
<cpp:element var="component" type="com.composum.pages.stage.model.edit.FrameComponent" mode="none">
    <div class="${componentCssBase}_toolset">
        <span class="${componentCssBase}_title">Resource Type</span>
        <div class="${componentCssBase}_type-actions ${componentCssBase}_actions btn-group btn-group-sm">
            <button type="button"
                    class="fa fa-edit ${componentCssBase}_edit-type composum-pages-tools_button btn btn-default"
                    title="Change component Type"><span
                    class="composum-pages-tools_button-label">Change</span></button>
        </div>
    </div>
    <span class="${componentCssBase}_type ${componentCssBase}_path alert alert-info">${component.pathHint}</span>
    <div class="${componentCssBase}_dialog">
        <sling:include resourceType="composum/pages/stage/edit/tools/component/edit/dialog"/>
    </div>
</cpp:element>
