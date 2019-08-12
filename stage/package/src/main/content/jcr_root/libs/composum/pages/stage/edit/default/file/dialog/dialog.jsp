<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog var="model" type="com.composum.pages.commons.model.GenericModel" languageContext="false"
                title="Edit Source" selector="generic" resourcePath="@{model.path}" submitLabel="Save"
                cssAdd="code-editor-dialog extra-wide">
    <cpp:widget name="#code" type="codearea" height="520px"/>
    <div class="code-editor-dialog_toolbar hidden">
        <div class="search input-group input-group-sm text-group">
            <input type="text" class="find-text form-control" placeholder="${cpn:i18n(slingRequest,'search in text')}">
            <span class="find-prev fa fa-chevron-left input-group-addon"
                  title="${cpn:i18n(slingRequest,'find previous')}"></span>
            <span class="find-next fa fa-chevron-right input-group-addon"
                  title="${cpn:i18n(slingRequest,'find next')}"></span>
        </div>
        <div class="flags">
            <div class="checkbox"><label><input type="checkbox" class="match-case"
                                                value="">${cpn:i18n(slingRequest,'match case')}</label>
            </div>
            <div class="checkbox"><label><input type="checkbox" class="find-regex"
                                                value="">${cpn:i18n(slingRequest,'regex')}
            </label></div>
        </div>
        <div class="replace input-group input-group-sm text-group">
            <input type="text" class="replace-text form-control"
                   placeholder="${cpn:i18n(slingRequest,'replace with...')}">
            <span class="replace fa fa-play input-group-addon" title="${cpn:i18n(slingRequest,'replace this')}"></span>
            <span class="replace-all fa fa-fast-forward input-group-addon"
                  title="${cpn:i18n(slingRequest,'replace all')}"></span>
        </div>
        <button type="button" class="undo fa fa-undo btn btn-default"
                title="${cpn:i18n(slingRequest,'undo last change')}"></button>
        <button type="button" class="redo fa fa-repeat btn btn-default"
                title="${cpn:i18n(slingRequest,'redo last undo')}"></button>
    </div>
</cpp:editDialog>
