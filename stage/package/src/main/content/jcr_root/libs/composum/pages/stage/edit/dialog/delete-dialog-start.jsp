<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<cpp:defineFrameObjects/>
<div${dialog.attributes}>
    <div class="modal-dialog">
        <div class="modal-content form-panel">
            <form class="widget-form ${dialogCSS}_form" method="${dialog.action.method}"
                  action="${dialog.action.url}" enctype="${dialog.action.encType}">
                <div class="modal-header ${dialogCSS}_header">
                    <button type="button" class="${dialogCSS}_button-close fa fa-close"
                            data-dismiss="modal" aria-label="Close"/>
                    <h4 class="modal-title ${dialogCSS}_dialog-title">
                        ${cpn:text(dialog.title)}
                    </h4>
                    <c:if test="${dialog.hasLanguageContext}">
                        <div class="${dialogCSS}_language">
                            <sling:include resourceType="composum/pages/stage/edit/tools/language/label"/>
                        </div>
                    </c:if>
                </div>
                <div class="modal-body ${dialogCSS}_content">
                    <div class="${dialogCSS}_messages messages">
                        <div class="panel panel-${dialog.alertKey}">
                            <div class="panel-heading">${dialog.alertText}</div>
                            <div class="panel-body hidden"></div>
                        </div>
                    </div>
                    <ul class="${dialogCSS}_tabs nav nav-tabs composum-commons-form-tab-nav">
                    </ul>
                    <input name="_charset_" type="hidden" value="UTF-8" class="${dialogCSS}_hidden"/>
                    <input name=":operation" type="hidden" value="delete" class="${dialogCSS}_hidden ${dialogCSS}_operation"/>
                    <input name="path" type="hidden" value="${dialog.editPath}" class="${dialogCSS}_hidden ${dialogCSS}_path"/>
                    <div class="${dialogCSS}_tabbed-content composum-commons-form-tabbed">
<!-- start of dialog content -->
