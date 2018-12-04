<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog var="sites" type="com.composum.pages.commons.model.Sites" selector="custom" title="Manage Sites">
    <div class="modal-header ${dialogCssBase}_header">
        <button type="button" class="${dialogCssBase}_button-close fa fa-close"
                data-dismiss="modal" aria-label="Close"></button>
        <h4 class="modal-title ${dialogCssBase}_dialog-title">${cpn:text(dialog.title)}</h4>
    </div>
    <div class="modal-body ${dialogCssBase}_content">
        <div class="${dialogCssBase}_messages messages">
            <div class="panel panel-${dialog.alertKey}">
                <div class="panel-heading">${dialog.alertText}</div>
                <div class="panel-body hidden"></div>
            </div>
        </div>
        <div class="${dialogCssBase}_sites-list">
            <cpp:include resourceType="composum/pages/stage/edit/site/manager"/>
        </div>
    </div>
    <div class="modal-footer ${dialogCssBase}_footer">
        <button type="button" class="${dialogCssBase}_button-add ${dialogCssBase}_button btn btn-default has-icon"
                data-dismiss="modal" title="${cpn:i18n(slingRequest,'Create Site')}"><i
                class="${dialogCssBase}_icon fa fa-plus"></i>${cpn:i18n(slingRequest,'Create')}</button>
        <button type="button" class="${dialogCssBase}_button-remove ${dialogCssBase}_button btn btn-default has-icon"
                data-dismiss="modal" title="${cpn:i18n(slingRequest,'Delete Site')}"><i
                class="${dialogCssBase}_icon fa fa-minus"></i>${cpn:i18n(slingRequest,'Delete')}</button>
        <button type="button" class="${dialogCssBase}_button-open ${dialogCssBase}_button btn btn-default has-icon"
                data-dismiss="modal" title="${cpn:i18n(slingRequest,'Open Site')}"><i
                class="${dialogCssBase}_icon fa fa-bullseye"></i>${cpn:i18n(slingRequest,'Open')}</button>
        <div class="${dialogCssBase}_hints"></div>
        <button type="button" class="${dialogCssBase}_button-cancel ${dialogCssBase}_button btn btn-default"
                data-dismiss="modal">${cpn:i18n(slingRequest,'Close')}</button>
    </div>
</cpp:editDialog>
