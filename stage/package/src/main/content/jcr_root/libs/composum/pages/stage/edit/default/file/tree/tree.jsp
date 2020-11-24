<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editToolbar var="model" type="com.composum.pages.stage.model.edit.FrameAsset">
    <div class="composum-pages-tools_button-group btn-group btn-group-sm" role="group">
        <cpp:treeMenu key="more" icon="navicon" label="More..." title="more file manipulation actions...">
            <cpp:menuItem icon="copy" label="Copy" title="Copy the selected file"
                          action="window.composum.pages.actions.file.copy"/>
            <cpp:menuItem icon="id-badge" label="Rename" title="Rename the selected file"
                          action="window.composum.pages.actions.file.rename"/>
            <cpp:menuItem icon="arrows-alt" label="Move" title="Move the selected file"
                          action="window.composum.pages.actions.file.move"/>
            <sling:call script="versionable.jsp"/>
            <cpp:menuItem icon="trash" label="Delete" title="Delete the selected file"
                          action="window.composum.pages.actions.file.delete"/>
        </cpp:treeMenu>
        <cpp:treeAction icon="upload" label="Upload" title="Upload new file content"
                        action="window.composum.pages.actions.file.upload"/>
    </div>
    <div class="composum-pages-tools_button-group btn-group btn-group-sm" role="group">
        <cpn:link title="${cpn:i18n(slingRequest,'Link to open or download the file')}" href="${resource.path}"
                  target="_blank" class="fa fa-download composum-pages-tools_button btn btn-default"><span
                class="composum-pages-tools_button-label">${cpn:i18n(slingRequest,'Open or Download')}</span></cpn:link>
    </div>
    <cpn:div test="${model.versionable}"
             class="composum-pages-tools_button-group btn-group btn-group-smaller" role="group">
        <sling:call script="activation.jsp"/>
    </cpn:div>
</cpp:editToolbar>
