<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<!DOCTYPE html>
<html ${currentPage.htmlLangAttribute} ${currentPage.htmlDirAttribute}
        class="${currentPage.htmlClasses}" data-context-path="${slingRequest.contextPath}">
<cpp:head>
    <sling:call script="head.jsp"/>
</cpp:head>
<cpp:body cssAdd="composum-pages-components-page">
    <sling:call script="body.jsp"/>
</cpp:body>
</html>
