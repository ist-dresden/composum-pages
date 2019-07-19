<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:model var="model" type="com.composum.pages.stage.model.edit.FrameModel">
    <div class="${modelCSS}_popover">
        <div class="${modelCSS}_time ${modelCSS}_release">
            <span class="name">${cpn:i18n(slingRequest,'release')}:</span><span
                class="text">${cpn:i18n(slingRequest,model.currentPage.releaseStatus.releaseLabel)}</span><span
                class="space">-</span><span
                class="state">${cpn:i18n(slingRequest,model.currentPage.releaseStatus.activationState)}</span>
        </div>
        <div class="${modelCSS}_time ${modelCSS}_last-modified">
            <hr/>
            <div class="line">
                <span class="name">${cpn:i18n(slingRequest,'last modified')}:</span><span
                    class="timestamp">${cpn:text(model.currentPage.releaseStatus.lastModified)}</span>
            </div>
            <div class="line">
                <span class="name">${cpn:i18n(slingRequest,'by')}:</span><span
                    class="user">${cpn:text(model.currentPage.releaseStatus.lastModifiedBy)}</span>
            </div>
        </div>
        <cpn:div test="${not empty model.currentPage.releaseStatus.lastActivated}"
                 class="${modelCSS}_time ${modelCSS}_last-activated">
            <hr/>
            <div class="line">
                <span class="name">${cpn:i18n(slingRequest,'last activated')}:</span><span
                    class="timestamp">${cpn:text(model.currentPage.releaseStatus.lastActivated)}</span>
            </div>
            <div class="line">
                <span class="name">${cpn:i18n(slingRequest,'by')}:</span><span
                    class="user">${cpn:text(model.currentPage.releaseStatus.lastActivatedBy)}</span>
            </div>
        </cpn:div>
        <cpn:div test="${not empty model.currentPage.releaseStatus.lastDeactivated}"
                 class="${modelCSS}_time ${modelCSS}_last-deactivated">
            <hr/>
            <div class="line">
                <span class="name">${cpn:i18n(slingRequest,'last deactivated')}:</span><span
                    class="timestamp">${cpn:text(model.currentPage.releaseStatus.lastDeactivated)}</span>
            </div>
            <div class="line">
                <span class="name">${cpn:i18n(slingRequest,'by')}:</span><span
                    class="user">${cpn:text(model.currentPage.releaseStatus.lastDeactivatedBy)}</span>
            </div>
        </cpn:div>
        <div class="${modelCSS}_time ${modelCSS}_repo-status">
            <hr/>
            <div class="line">
                <span class="text-${model.currentPage.checkedOut?'success':'danger'}">${cpn:i18n(slingRequest,model.currentPage.checkedOut?'checked out':'checked in')}</span>,
                <cpn:div test="${model.currentPage.locked}" tagName="span"
                         class="text-danger">${cpn:i18n(slingRequest,'locked')} ${cpn:i18n(slingRequest,'by')} ${cpn:text(model.currentPage.lockOwner)}</cpn:div>
                <cpn:div test="${!model.currentPage.locked}" tagName="span"
                         class="text-success">${cpn:i18n(slingRequest,'unlocked')}</cpn:div>
            </div>
        </div>
    </div>
</cpp:model>
