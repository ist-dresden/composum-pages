<%@page session="false" pageEncoding="utf-8" %><%--
--%><%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><cpp:defineFrameObjects/>
<button type="button"
        class="fa fa-navicon composum-pages-tools_button btn btn-default dropdown dropdown-toggle"
        data-toggle="dropdown" title="${cpn:i18n(slingRequest,'More actions...')}"><span
        class="composum-pages-tools_button-label">${cpn:i18n(slingRequest,'More...')}</span></button>
<ul class="composum-pages-tools_menu dropdown-menu" role="menu">
    <li><a href="#" class="${treeCssBase}_rename" data-action="window.composum.pages.actions.page.rename"
           title="${cpn:i18n(slingRequest,'Rename')}Rename/Move the selected page">${cpn:i18n(slingRequest,'Rename')}</a>
    </li>
    <li><a href="#" class="${treeCssBase}_move" data-action="window.composum.pages.actions.page.move"
           title="${cpn:i18n(slingRequest,'Move')}Rename/Move the selected page">${cpn:i18n(slingRequest,'Move')}</a>
    </li>
    <li><a href="#" class="${treeCssBase}_checkout" data-action="window.composum.pages.actions.page.checkout"
           title="${cpn:i18n(slingRequest,'Checkout/Checkin')} the selected page">${cpn:i18n(slingRequest,'Checkout')}</a>
    </li>
    <li><a href="#" class="${treeCssBase}_lock" data-action="window.composum.pages.actions.page.lock"
           title="${cpn:i18n(slingRequest,'Lock/Unlock the selected page')}">${cpn:i18n(slingRequest,'Lock / Unlock')}</a>
    </li>
</ul>
<button type="button"
        class="fa fa-edit ${treeCssBase}_create composum-pages-tools_button btn btn-default"
        title="${cpn:i18n(slingRequest,'Edit the page prroperties')}"
        data-action="window.composum.pages.actions.page.edit"><span
        class="composum-pages-tools_button-label">${cpn:i18n(slingRequest,'Create')}</span></button>
