<%@page session="false" pageEncoding="UTF-8"%>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<cpp:defineFrameObjects/>

<div class="composum-pages-tools">
    <div class="composum-pages-tools_actions btn-toolbar">
        <div class="composum-pages-tools_left-actions">
            <input type="checkbox" class="composum-pages-stage-edit-tools-site-unversioned_tools-selectall" name="composum-pages-stage-edit-tools-site-unreleased_tools-selectall" />
        </div>
        <div class="composum-pages-tools_right-actions">
            <div class="composum-pages-tools_button-group btn-group btn-group-sm" role="group">
                <button type="button"
                        class="fa fa-circle-o composum-pages-stage-edit-tools-site-unversioned_tools-ckeckpoint composum-pages-tools_button btn btn-default"
                        title="New Checkpoint">
                    <span class="composum-pages-tools_button-label">Checkpoint</span></button>
            </div>
        </div>
    </div>

    <div class="composum-pages-stage-edit-tools-site-unversioned_tools-panel">
        <%-- <sling:include replaceSelectors="content"/> - on demand using Ajax --%>
    </div>
</div>
