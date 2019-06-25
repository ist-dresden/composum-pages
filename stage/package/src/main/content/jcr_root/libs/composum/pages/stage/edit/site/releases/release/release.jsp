<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineFrameObjects/>
<cpp:element var="release" type="com.composum.pages.commons.model.SiteRelease"
             tagName="tr">
    <c:if test="${release.editMode}">
        <td class="${releaseCSS}_input"><input type="radio" class="${releaseCSS}_select"
                                               name="${releaseCSS}_select"
                                               value="${release.key}" data-path="${release.path}"/></td>
    </c:if>
    <td class="${releaseCSS}_content">
        <a class="collapsed" role="button" data-toggle="collapse" id="#release_head_${release.key}"
           href="#release_detail_${release.key}" aria-expanded="false" aria-controls="release_detail_${release.key}">
            <cpn:text class="${releaseCSS}_key">${release.key}</cpn:text>
            <cpn:text class="${releaseCSS}_title">${release.title}</cpn:text>
            <cpn:text test="${not release.current}" class="${releaseCSS}_creationDate date" format="created: {}"
                      i18n="true" value="${release.creationDateString}"/>
            <div class="${releaseCSS}_categories">
                <c:forEach items="${release.categories}" var="category">
                    <span class="label label-primary ${releaseCSS}_category">${category}</span>
                </c:forEach>
            </div>
        </a>
        <cpn:div test="${release.current}" class="${releaseCSS}_actions btn-group" role="group" aria-label="...">
            <button type="button" class="btn btn-default release-finalize"><i
                    class="fa fa-flag-checkered"></i>${cpn:i18n(slingRequest,'Finalize')}</button>
        </cpn:div>
        <div id="release_detail_${release.key}" class="collapse fade${release.current?' in':''}"
             aria-labelledby="release_head_${release.key}">
            <cpn:text class="${releaseCSS}_description">${release.description}</cpn:text>
        </div>
    </td>
</cpp:element>
