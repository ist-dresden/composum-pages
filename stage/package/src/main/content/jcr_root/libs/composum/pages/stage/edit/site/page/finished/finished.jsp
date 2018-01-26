<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineFrameObjects/>
<cpp:model var="site" type="com.composum.pages.commons.model.Site" mode="none"
           cssBase="composum-pages-stage-edit-site-page-finished">
    <div class="panel panel-default finishedPages" data-path="${site.path}">
        <div class="panel-heading" role="tab" id="finishedPagesHead">
            <h4 class="panel-title">
                <a class="collapsed" role="button" data-toggle="collapse"
                   href="#finishedPagesPanel" aria-expanded="false"
                   aria-controls="finishedPagesPanel">
                    Finished Objects (version after last release)
                </a>
            </h4>
            <c:if test="${site.editMode}">
                <div class="btn-group" role="group" aria-label="...">
                    <button type="button" class="btn btn-default release">Do Release...
                    </button>
                </div>
            </c:if>
        </div>
        <div id="finishedPagesPanel" class="panel-collapse collapse in" role="tabpanel"
             aria-labelledby="finishedPagesHead">
            <div class="panel-body">
                <table class="${siteCssBase}_table table">
                    <thead class="${siteCssBase}_thead">
                    <tr>
                        <c:if test="${site.editMode}">
                            <th><input type="checkbox" class="${siteCssBase}_select-all"/></th>
                        </c:if>
                        <th class="${siteCssBase}_key">rel. Path</th>
                        <th class="${siteCssBase}_title">Title</th>
                        <th class="${siteCssBase}_description">modification Date</th>
                    </tr>
                    </thead>
                    <tbody class="${siteCssBase}_tbody">
                    <c:forEach items="${site.unreleasedPages}" var="page">
                        <tr>
                            <c:if test="${site.editMode}">
                                <td class="${siteCssBase}_select"><input type="checkbox" class="${siteCssBase}_select"
                                                                         name="${siteCssBase}_select"
                                                                         data-path="${page.path}"></td>
                            </c:if>
                            <td class="${siteCssBase}_page-path">${page.siteRelativePath}</td>
                            <td class="${siteCssBase}_page-title">${page.title}</td>
                            <td class="${siteCssBase}_page.time">${page.lastModifiedString}</td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</cpp:model>
