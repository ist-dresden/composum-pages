<%@page session="false" pageEncoding="utf-8" %><%--
--%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %><%--
--%><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%--
--%><cpp:defineFrameObjects/>
<cpp:model var="component" type="com.composum.pages.commons.model.Component">
    <c:choose>
        <c:when test="${component.editDialog.hasThumbnailImage}">
            <div class="composum-pages-edit-thumbnail-image_dialog composum-pages-edit-thumbnail-image">
                <cpn:image src="${component.editDialog.thumbnailImage.path}"/>
            </div>
        </c:when>
        <c:otherwise>
            <div class="composum-pages-edit-thumbnail_dialog composum-pages-edit-thumbnail">
                <sling:include replaceSelectors=""/><%-- include the dialog itself as 'thumbnail' --%>
            </div>
        </c:otherwise>
    </c:choose>
</cpp:model>
