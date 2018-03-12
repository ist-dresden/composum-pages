<%@page session="false" pageEncoding="UTF-8"%>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<cpp:model var="site" type="com.composum.pages.commons.model.Site" mode="none"
           cssBase="composum-pages-stage-site_tile">
    <cpp:include resourceType="composum/pages/stage/edit/default/site/thumbnail"/>
    <div class="${siteCssBase}_text">
        <cpn:text tagName="h3" value="${site.title}" i18n="true" tagClass="${siteCssBase}_title"/>
        <cpn:text tagName="div" value="${site.description}" type="rich" i18n="true"
                  tagClass="${siteCssBase}_description"/>
        <cpn:text tagName="h4" value="${site.path}" tagClass="${siteCssBase}_path"/>
    </div>
</cpp:model>
