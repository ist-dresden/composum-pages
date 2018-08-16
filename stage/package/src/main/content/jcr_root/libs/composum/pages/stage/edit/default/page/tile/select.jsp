<%@page session="false" pageEncoding="UTF-8"%>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<cpp:model var="tilepage" type="com.composum.pages.commons.model.Page" mode="none"
           cssBase="composum-pages-stage-page_tile">
    <cpn:link class="${tilepageCssBase}" href="/bin/pages.html${tilepage.path}">
        <sling:call script="_page.jsp"/>
    </cpn:link>
</cpp:model>
