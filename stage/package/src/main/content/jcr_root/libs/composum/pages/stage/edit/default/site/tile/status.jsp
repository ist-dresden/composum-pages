<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineObjects/>
<cpp:model var="site" type="com.composum.pages.commons.model.Site" mode="none"
           cssBase="composum-pages-stage-site_tile">
    <div class="${siteCssBase}">
        <cpp:include resourceType="composum/pages/stage/edit/default/site/thumbnail"/>
        <div class="${siteCssBase}_text">
            <h3 class="${siteCssBase}_title">${cpn:text(site.title)}<c:if test="${site.templatePath}"><span
                    class="${siteCssBase}_template">*</span></c:if></h3>
            <div class="${siteCssBase}_status">
                <a href="#"
                   class="badge badge-pill modified"
                   title="${cpn:i18n(slingRequest,'Modified Pages')}"><%=site.getModifiedPages().size()%>
                </a><a href="#"
                       class="badge badge-pill unreleased"
                       title="${cpn:i18n(slingRequest,'Finished Pages')}"><%=site.getUnreleasedPages().size()%>
            </a>
            </div>
            <h4 class="${siteCssBase}_path">${cpn:path(site.path)}</h4>
        </div>
    </div>
</cpp:model>
