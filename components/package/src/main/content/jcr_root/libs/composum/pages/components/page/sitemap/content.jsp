<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<div class="composum-pages-components-page_content">
    <div class="composum-pages-components-page_content_top">
        <cpp:include path="top" resourceType="composum/pages/components/container/parsys"/>
    </div>
    <div class="composum-pages-components-page_content_main">
        <div class="composum-pages-components-container">
            <div class="composum-pages-components-container-row">
                <div class="composum-pages-components-container-row_column composum-pages-components-container-row_column-first col col-lg-12 col-md-12 col-sm-12 col-xs-12">
                    <div class="composum-pages-components-container">
                        <cpp:include resourceType="composum/pages/components/page/sitemap" replaceSelectors="embedded"
                                     mode="none"/>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class="composum-pages-components-page_content_bottom">
        <cpp:include path="bottom" resourceType="composum/pages/components/container/iparsys"/>
    </div>
</div>
