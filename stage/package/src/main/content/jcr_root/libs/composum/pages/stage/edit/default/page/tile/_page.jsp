<%@page session="false" pageEncoding="UTF-8"%>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<cpp:model var="tilepage" type="com.composum.pages.commons.model.Page" mode="none"
           cssBase="composum-pages-stage-page_tile">
    <cpp:include resourceType="composum/pages/stage/edit/default/page/thumbnail"/>
    <div class="${tilepageCssBase}_text">
        <cpn:text tagName="h3" value="${tilepage.title}" i18n="true" class="${tilepageCssBase}_title"/>
        <cpn:text tagName="div" value="${tilepage.description}" type="rich" i18n="true"
                  class="${tilepageCssBase}_description"/>
        <cpn:text tagName="h4" value="${tilepage.path}" class="${tilepageCssBase}_path"/>
    </div>
</cpp:model>
