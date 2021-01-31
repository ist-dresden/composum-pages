<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<cpp:model var="reference" type="com.composum.pages.components.model.reference.ReferencePreview">
    <html data-context-path="${slingRequest.contextPath}">
    <head>
        <cpn:clientlib type="css" category="${reference.containingPage.viewClientlibCategory}"/>
        <cpn:clientlib type="css" category="composum.pages.components.frame"/>
    </head>
    <body class="composum-pages-components-page">
    <div class="composum-pages-components-element-reference_preview-content">
        <cpp:include path="${reference.includePath}" mode="none"/>
    </div>
    </body>
    </html>
</cpp:model>
