<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<cpp:model var="pageModel" type="com.composum.pages.options.microsite.model.MicrositePage" scope="request">
    <html ${pageModel.htmlLangAttribute} ${pageModel.htmlDirAttribute}
            class="${pageModel.htmlClasses}" data-context-path="${slingRequest.contextPath}">
    <cpp:head>
        <sling:call script="head.jsp"/>
    </cpp:head>
    <cpp:body cssAdd="composum-pages-options-microsite-page">
        <div class="${pageModelCSS}_data">
            <table class="table">
                <tr>
                    <th colspan="2">Composum Pages Options Microsite</th>
                </tr>
                <tr>
                    <td>last import</td>
                    <td>${cpn:text(pageModel.lastImportTime)}</td>
                </tr>
                <tr>
                    <td>filename</td>
                    <td>${cpn:text(pageModel.properties.lastImportFile)}</td>
                </tr>
                <tr>
                    <td>file size</td>
                    <td>${cpn:text(pageModel.properties.lastImportSize)}</td>
                </tr>
                <tr>
                    <td>index path</td>
                    <td>${cpn:text(pageModel.properties.indexPath)}</td>
                </tr>
            </table>
        </div>
        <div class="${pageModelCSS}_preview">
            <iframe class="${pageModelCSS}_frame" width="100%" src="${pageModel.embeddedPreviewUrl}"></iframe>
        </div>
    </cpp:body>
    </html>
</cpp:model>
