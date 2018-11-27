<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<cpp:model var="page_content" type="com.composum.pages.commons.model.PageContent" scope="request">
    <div class="composum-pages-components-page_content">
        <div class="composum-pages-components-page_content_top">
            <div class="composum-pages-components-page_image">
                <cpp:include path="image" resourceType="composum/pages/components/element/image/background"/>
            </div>
            <div class="composum-pages-components-page_teaser">
                <cpp:include path="teaser" resourceType="composum/pages/components/time/event/teaser"/>
            </div>
            <div class="composum-pages-components-page_map">
                <cpp:include path="map" resourceType="composum/pages/components/element/map/google"/>
            </div>
        </div>
        <div class="composum-pages-components-page_content_main">
            <cpp:include path="main" resourceType="composum/pages/components/container/parsys"/>
        </div>
    </div>
</cpp:model>
