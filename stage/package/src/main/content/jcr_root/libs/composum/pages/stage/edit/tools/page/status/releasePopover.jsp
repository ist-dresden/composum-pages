<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:model var="model" type="com.composum.pages.stage.model.edit.FrameModel">
    <div class="${modelCssBase}_popover">
        <div class="${modelCssBase}_time ${modelCssBase}_release">
            <span class="name">${cpn:i18n(slingRequest,'release')}:</span><span
                class="text">${cpn:i18n(slingRequest,model.currentPage.releaseStatus.releaseLabel)}</span><span
                class="space">-</span><span
                class="state">${cpn:i18n(slingRequest,model.currentPage.releaseStatus.activationState)}</span>
        </div>
        <div class="${modelCssBase}_time ${modelCssBase}_last-modified">
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
                 class="${modelCssBase}_time ${modelCssBase}_last-activated">
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
                 class="${modelCssBase}_time ${modelCssBase}_last-deactivated">
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
    </div>
</cpp:model>
