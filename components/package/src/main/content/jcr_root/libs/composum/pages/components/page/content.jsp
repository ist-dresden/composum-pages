<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<div class="composum-pages-components-page_content">
    <div class="composum-pages-components-page_content_top">
        <cpp:include path="top" resourceType="composum/pages/components/container/parsys"/>
    </div>
    <cpp:include resourceType="composum/pages/components/navigation/breadcrumbs"/>
    <div class="composum-pages-components-page_content_main">
        <cpp:include path="main" resourceType="composum/pages/components/container/parsys"/>
    </div>
    <div class="composum-pages-components-page_content_bottom">
        <cpp:include path="bottom" resourceType="composum/pages/components/container/iparsys"/>
    </div>
</div>
