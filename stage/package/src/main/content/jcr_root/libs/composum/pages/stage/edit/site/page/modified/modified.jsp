<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineFrameObjects/>
<div class="panel panel-default">
    <div class="panel-heading" role="tab" id="openPagesHead">
        <h4 class="panel-title">
            <a role="button" data-toggle="collapse" href="#openPagesPanel"
               aria-expanded="true" aria-controls="openPagesPanel">
                Open Objects (changed and not versioned)
            </a>
        </h4>
    </div>
    <div id="openPagesPanel" class="panel-collapse collapse in" role="tabpanel"
         aria-labelledby="openPagesHead">
        <div class="panel-body">
            <cpp:model var="site" type="com.composum.pages.commons.model.Site" mode="none"
                       cssBase="composum-pages-stage-edit-site-page-modified">
                <table class="${siteCssBase}_table table">
                    <thead class="${siteCssBase}_thead">
                    <tr>
                        <th class="${siteCssBase}_key">rel. Path</th>
                        <th class="${siteCssBase}_title">Title</th>
                        <th class="${siteCssBase}_description">modification Date</th>
                    </tr>
                    </thead>
                    <tbody class="${siteCssBase}_tbody">
                    <c:forEach items="${site.modifiedPages}" var="page">
                        <tr>
                            <td class="${siteCssBase}_page-path">${page.siteRelativePath}</td>
                            <td class="${siteCssBase}_page-title">${page.title}</td>
                            <td class="${siteCssBase}_page-time">${page.lastModifiedString}</td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </cpp:model>
        </div>
    </div>
</div>
