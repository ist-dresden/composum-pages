<%@page session="false" pageEncoding="UTF-8"
        import="static com.composum.pages.commons.PagesConstants.RA_STICKY_LOCALE" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<% request.setAttribute(RA_STICKY_LOCALE, request.getLocale()); // use editors locale %>
<cpp:model var="element" type="com.composum.pages.stage.model.edit.FrameElement"
           cssBase="composum-pages-tools">
    <div class="${elementCssBase}_help-view">
        <div class="composum-pages-help-page_body">
            <cpp:include replaceSelectors="header"/>
            <cpp:include path="${element.component.helpContent}" mode="none"
                         resourceType="composum/pages/stage/edit/tools/component/help/page" replaceSelectors="parsys"/>
        </div>
    </div>
</cpp:model>
<% request.removeAttribute(RA_STICKY_LOCALE); %>
