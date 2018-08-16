<%@page session="false" pageEncoding="UTF-8"%>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<cpp:defineFrameObjects/>
<cpp:element var="site" type="com.composum.pages.stage.model.edit.site.SiteElement" mode="none"
             cssBase="composum-pages-stage-edit-site-page-finished" data-path="@{site.site.path}">
    <div class="composum-pages-tools_actions btn-toolbar">
        <div class="composum-pages-tools_left-actions">
            <input type="checkbox" class="composum-pages-stage-edit-site-page-finished_page-select-all"/>
            <cpn:text tagName="label" value="Finished Pages" i18n="true"/>
        </div>
        <div class="composum-pages-tools_right-actions">
            <div class="composum-pages-tools_button-group btn-group btn-group-sm" role="group">
                <button type="button"
                        class="fa fa-flag-checkered release composum-pages-tools_button btn btn-default"
                        title="Do Release...">
                    <span class="composum-pages-tools_button-label">Release</span></button>
            </div>
        </div>
    </div>
    <div class="composum-pages-stage-edit-tools-site-finished_tools-panel">
        <ul class="${siteCssBase}_list">
            <c:forEach items="${site.site.unreleasedPages}" var="page">
                <li class="${siteCssBase}_listentry">
                    <input type="checkbox" class="${siteCssBase}_page-select" data-path="${page.path}"/>
                    <div class="${siteCssBase}_page-entry" data-path="${page.path}">
                        <div class="${siteCssBase}_page-head">
                            <div class="${siteCssBase}_page-title">${page.title}</div>
                            <div class="${siteCssBase}_page-time">${page.lastModifiedString}</div>
                        </div>
                        <div class="${siteCssBase}_page-path">${page.siteRelativePath}</div>
                    </div>
                </li>
            </c:forEach>
        </ul>
    </div>
</cpp:element>
