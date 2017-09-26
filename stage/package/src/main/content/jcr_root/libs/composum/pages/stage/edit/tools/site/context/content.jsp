<%@page session="false" pageEncoding="UTF-8"%><%--
--%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%--
--%><cpp:defineFrameObjects/>

<cpp:element var="site" type="com.composum.pages.stage.model.edit.site.SiteElement" mode="none">
    <label>Releases</label>
    <div class="composum-pages-tools_right-actions">
        <div class="composum-pages-tools_button-group btn-group btn-group-sm" role="group">
            <button type="button"
                    class="fa fa-check-square composum-pages-stage-edit-tools-site-context_tools-publicrelease composum-pages-tools_button btn btn-default"
                    title="Public Release..."></button>
            <button type="button"
                    class="fa fa-eye composum-pages-stage-edit-tools-site-context_tools-previewrelease composum-pages-tools_button btn btn-default"
                    title="Preview Release..."></button>
            <button type="button"
                    class="fa fa-trash composum-pages-stage-edit-tools-site-context_tools-deleterelease composum-pages-tools_button btn btn-default"
                    title="Delete Release..."></button>
        </div>
    </div>
    <ul class="${siteCssBase}_list">
    <c:forEach items="${site.site.releases}" var="release">
        <sling:include resource="${release.resource}" resourceType="composum/pages/stage/edit/tools/site/context/release"/>
    </c:forEach>
</cpp:element>

<cpp:element var="languages" type="com.composum.pages.stage.model.edit.site.Languages" mode="none">
    <label>Languages</label>
    <ul class="${languagesCssBase}_list">
    <c:forEach items="${languages.languageList}" var="language">
        <sling:include resource="${language}" resourceType="composum/pages/stage/edit/tools/site/context/language"/>
    </c:forEach>
</cpp:element>
