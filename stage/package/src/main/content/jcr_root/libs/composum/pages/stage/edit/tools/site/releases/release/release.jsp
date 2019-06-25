<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineFrameObjects/>
<cpp:model var="release" type="com.composum.pages.commons.model.SiteRelease" mode="none"
           cssBase="composum-pages-stage-edit-site-releases-release">
    <li class="${releaseCSS} ${releaseCSS}_listentry">
        <input type="radio" class="${releaseCSS}_select" name="${releaseCSS}_select"
               value="${release.key}" data-path="${release.path}"/>
        <div class="${releaseCSS}_entry" data-path="${release.path}">
            <div class="${releaseCSS}_head">
                <cpn:text class="${releaseCSS}_key">${release.key}</cpn:text>
                <cpn:text class="${releaseCSS}_title">${release.titleString}</cpn:text>
            </div>
            <div class="${releaseCSS}_status">
                <cpn:text test="${not release.current}" class="${releaseCSS}_creationDate date"
                          value="${release.creationDateString}"/>
                <cpn:div test="${release.current}" class="${releaseCSS}_actions btn-group" role="group"
                         aria-label="...">
                    <button type="button"
                            class="fa fa-flag-checkered btn btn-default release-finalize composum-pages-tools_button"
                            title="${cpn:i18n(slingRequest,'Finalize')}"></button>
                </cpn:div>
                <div class="${releaseCSS}_categories">
                    <c:forEach items="${release.categories}" var="category">
                        <span class="label label-primary ${releaseCSS}_category">${category}</span>
                    </c:forEach>
                </div>
            </div>
        </div>
    </li>
</cpp:model>
