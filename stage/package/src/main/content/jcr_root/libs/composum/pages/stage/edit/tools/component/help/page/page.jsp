<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<html ${currentPage.htmlLangAttribute} ${currentPage.htmlDirAttribute}
        class="${currentPage.htmlClasses}" data-context-path="${slingRequest.contextPath}">
<cpp:head>
    <cpn:clientlib type="css" category="composum.pages.help.view"/>
    <cpn:clientlib type="css" test="${currentPage.editMode}" category="composum.pages.help.edit"/>
</cpp:head>
<cpp:body>
    <cpp:include replaceSelectors="content"/>
    <cpn:clientlib type="js" category="composum.pages.help.view"/>
    <cpn:clientlib type="js" test="${currentPage.editMode}" category="composum.pages.help.edit"/>
</cpp:body>
</html>
