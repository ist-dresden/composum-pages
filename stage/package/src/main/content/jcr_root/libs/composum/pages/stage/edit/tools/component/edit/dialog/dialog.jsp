<%@page session="false" pageEncoding="UTF-8"%><%--
--%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%--
--%><cpp:defineFrameObjects/>
<cpp:element var="component" type="com.composum.pages.stage.model.edit.FrameComponent" mode="none"
               cssClasses="@{componentCssBase}@{component.editDialog.inherited?'_inherited':''}"
               data-path="@{component.editDialog.path}/dialog.jsp" data-type="jsp">
    <div class="composum-pages-stage-edit-tools-dev-component_toolset">
        <span class="composum-pages-stage-edit-tools-dev-component_title">Edit Dialog<c:if
            test="${component.editDialog.inherited}"><i class="fa fa-level-down"></i></c:if></span>
        <div class="${componentCssBase}_actions composum-pages-stage-edit-tools-dev-component_actions btn-group btn-group-sm">
            <button type="button"
                    class="fa fa-edit ${componentCssBase}_edit composum-pages-tools_button btn btn-default"
                    title="Edit dialog template"><span
                    class="composum-pages-tools_button-label">Edit</span></button>
        </div>
    </div>
    <c:if test="${component.editDialog.inherited}">
        <span class="${componentCssBase}_inherited ${componentCssBase}_path alert alert-warning">${component.editDialog.pathHint}</span>
    </c:if>
    <c:choose>
        <c:when test="${component.editDialog.valid}">
            <div class="${componentCssBase}_thumbnail">
                <sling:include path="${component.path}"
                               resourceType="${component.editDialog.path}" replaceSelectors="thumbnail"/>
            </div>
        </c:when>
        <c:otherwise>
            <div class="${componentCssBase}_missed alert alert-warning">
                No dialog component found!
            </div>
        </c:otherwise>
    </c:choose>
</cpp:element>
