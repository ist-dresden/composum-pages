<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<!-- end of page content -->
    <div class="composum-pages-stage-edit-handles">
        <div class="composum-pages-component-handle_pointer">
            <sling:include resourceType="composum/pages/stage/edit/page/component/handle"/>
        </div>
        <div class="composum-pages-component-handle_selection">
            <sling:include resourceType="composum/pages/stage/edit/page/component/handle"/>
        </div>
        <sling:include resourceType="composum/pages/stage/edit/page/component/dnd"/>
    </div>
<sling:include replaceSelectors="body-edit"/>
</body>
