<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineFrameObjects/>
<cpp:model var="release" type="com.composum.pages.commons.model.SiteRelease" mode="none"
           cssBase="composum-pages-site-tools_releases_release">
    <li class="${releaseCSS} ${releaseCSS}-listentry">
        <div class="_release-state"><input type="radio" name="${releaseCSS}-select" value="${release.key}"
                                           data-path="${release.path}" data-label="${release.title}"
                                           class="${releaseCSS}-select${release.public?' is-public':''}${release.preview?' is-preview':''}"/>
        </div>
        <div class="${releaseCSS}-entry" data-path="${release.path}">
            <div class="${releaseCSS}-head">
                <cpn:text class="${releaseCSS}-key">${release.key}</cpn:text>
                <cpn:text class="${releaseCSS}-title">${release.titleString}</cpn:text>
            </div>
            <div class="${releaseCSS}-status">
                <cpn:text test="${not release.current}" class="${releaseCSS}-creationDate date"
                          value="${release.creationDateString}"/>
                <cpn:div test="${release.current}" class="${releaseCSS}-actions btn-group" role="group"
                         aria-label="...">
                    <button type="button"
                            class="fa fa-flag-checkered btn btn-default release-finalize composum-pages-tools_button"
                            title="${cpn:i18n(slingRequest,'Finalize')}"></button>
                </cpn:div>
                <div class="${releaseCSS}-categories">
                    <c:forEach items="${release.categories}" var="category">
                        <span class="label label-primary ${releaseCSS}-category">${category}</span>
                    </c:forEach>
                </div>
            </div>
        </div>
    </li>
</cpp:model>
