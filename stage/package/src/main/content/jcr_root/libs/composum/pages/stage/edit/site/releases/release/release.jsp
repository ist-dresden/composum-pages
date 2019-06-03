<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineFrameObjects/>
<cpp:element var="release" type="com.composum.pages.commons.model.SiteRelease"
             tagName="tr">
    <c:if test="${release.editMode}">
        <td class="${releaseCssBase}_input"><input type="radio" class="${releaseCssBase}_select"
                                                   name="${releaseCssBase}_select"
                                                   value="${release.key}" data-path="${release.path}"/></td>
    </c:if>
    <td class="${releaseCssBase}_content">
        <a class="collapsed" role="button" data-toggle="collapse" id="#release_head_${release.key}"
           href="#release_detail_${release.key}" aria-expanded="false" aria-controls="release_detail_${release.key}">
            <cpn:text class="${releaseCssBase}_key">${release.key}</cpn:text>
            <cpn:text class="${releaseCssBase}_title">${release.title}</cpn:text>
        </a>
        <div id="release_detail_${release.key}" class="collapse fade${release.current?' in':''}"
             aria-labelledby="release_head_${release.key}">
            <cpn:text class="${releaseCssBase}_description">${release.description}</cpn:text>
            <div class="${releaseCssBase}_categories">
                <c:forEach items="${release.categories}" var="category">
                    <cpn:text class="label label-primary">${category}</cpn:text>
                </c:forEach>
            </div>
            <cpn:div test="${release.current}" class="${releaseCssBase}_actions">
                <div class="btn-group" role="group" aria-label="...">
                    <button type="button" class="btn btn-default release-finalize"><i
                            class="fa fa-flag-checkered"></i>${cpn:i18n(slingRequest,'Finalize')}</button>
                </div>
            </cpn:div>
        </div>
    </td>
</cpp:element>
