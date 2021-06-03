<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<div class="composum-pages-components-page_content">
    <cpp:include resourceType="composum/pages/components/navigation/breadcrumbs"/>
    <div class="composum-pages-components-page_content-row row">
        <a class="composum-pages-components-page_content_nav_toggle fa fa-bars" href="#"></a>
        <nav class="composum-pages-components-page_content_nav col-lg-3 col-md-12">
            <cpp:include resourceType="composum/pages/components/navigation/sidebar" replaceSelectors="showroot"/>
        </nav>
        <main class="composum-pages-components-page_content_main col-lg-6 col-md-9 col-sm-12">
            <div class="composum-pages-components-page_main-top">
                <cpp:include resourceType="composum/pages/options/blog/components/static/intro"/>
            </div>
            <div class="composum-pages-components-page_main-body">
                <cpp:include path="main" resourceType="composum/pages/components/container/parsys"/>
            </div>
        </main>
        <aside class="composum-pages-components-page_content_aside col-lg-3 col-md-3 col-sm-12">
            <cpp:include path="aside" resourceType="composum/pages/components/container/iparsys"/>
        </aside>
    </div>
    <div class="composum-pages-components-page_content_bottom">
        <cpp:include path="bottom" resourceType="composum/pages/components/container/iparsys"/>
    </div>
</div>
