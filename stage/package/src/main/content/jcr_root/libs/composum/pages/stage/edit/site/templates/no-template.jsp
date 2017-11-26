<%@page session="false" pageEncoding="UTF-8"%><%--
--%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %><%--
--%><%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%--
--%><cpp:defineObjects/>
<cpp:model var="site" type="com.composum.pages.commons.model.Site" mode="none"
           cssBase="composum-pages-stage-site_tile">
    <cpn:link class="${siteCssBase}" href="#">
        <cpp:include resourceType="composum/pages/stage/edit/default/site/thumbnail"/>
        <div class="${siteCssBase}_text">
            <cpn:text tagName="h3" value="no template" i18n="true" tagClass="${siteCssBase}_title"/>
            <cpn:text tagName="div" value="<p>Create a new site without a template.</p>"
                      type="rich" i18n="true" tagClass="${siteCssBase}_description"/>
        </div>
    </cpn:link>
</cpp:model>
