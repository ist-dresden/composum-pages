<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:element var="deleteRelease" type="com.composum.pages.stage.model.edit.site.DeleteRelease" >
    <div id="deleterelease-dialog" class="dialog modal fade" role="dialog" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content form-panel">
                <cpn:form class="widget-form ${deleteReleaseCssBase}_form" method="POST" action="*">
                    <div class="modal-header ${deleteReleaseCssBase}_header">
                        <button type="button" class="${deleteReleaseCssBase}_button-close fa fa-close close" data-dismiss="modal" aria-label="Close"></button>
                        <h4 class="modal-title ${deleteReleaseCssBase}_dialog-title">Delete Release</h4>
                    </div>
                    <div class="modal-body ${deleteReleaseCssBase}_content">
                        <div class="${deleteReleaseCssBase}_messages messages">
                            <div class="${deleteReleaseCssBase}_alert alert alert-hidden"></div>
                        </div>
                        <input name="_charset_" type="hidden" value="UTF-8" class="${deleteReleaseCssBase}_hidden"/>
                        <input name="path" type="hidden" value="${resource.path}" class="${deleteReleaseCssBase}_hidden ${deleteReleaseCssBase}_path"/>
                        <label class="control-label">Do you really want to delete this release?</label>
                        <input name="releaseName" type="text" class="widget text-field-widget form-control ${deleteReleaseCssBase}_releaseName" readonly/>
                    </div>
                    <div class="modal-footer ${deleteReleaseCssBase}_footer">
                        <button type="button" class="btn btn-default ${deleteReleaseCssBase}_button-cancel" data-dismiss="modal">Cancel</button>
                        <button type="submit" class="btn btn-primary create ${deleteReleaseCssBase}_button-delete">Delete</button>
                    </div>
                </cpn:form>
            </div>
        </div>
    </div>
</cpp:element>