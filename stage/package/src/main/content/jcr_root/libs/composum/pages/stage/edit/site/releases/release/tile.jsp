<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineFrameObjects/>
<cpp:model var="model" type="com.composum.pages.stage.model.edit.site.ReleaseModel">
    <div class="${modelCSS}_tile">
        <div class="${modelCSS}_row">
            <cpn:text class="${modelCSS}_title">${model.release.titleString}</cpn:text>
            <cpn:text class="${modelCSS}_key">${model.release.key}</cpn:text>
        </div>
        <div class="${modelCSS}_row">
            <div class="${modelCSS}_categories">
                <c:forEach items="${model.release.categories}" var="category">
                    <span class="label label-primary ${modelCSS}_category">${category}</span>
                </c:forEach>
            </div>
            <cpn:text test="${not model.release.current}" class="${modelCSS}_creationDate date" format="created: {}"
                      i18n="true" value="${model.release.creationDateString}"/>
            <cpn:div test="${model.release.current}" class="${modelCSS}_actions btn-group" role="group"
                     aria-label="...">
                <button type="button" class="btn btn-default release-finalize"><i
                        class="fa fa-flag-checkered"></i>${cpn:i18n(slingRequest,'Finalize')}</button>
            </cpn:div>
        </div>
        <cpn:text class="${modelCSS}_description" type="rich">${model.release.description}</cpn:text>
    </div>
</cpp:model>
