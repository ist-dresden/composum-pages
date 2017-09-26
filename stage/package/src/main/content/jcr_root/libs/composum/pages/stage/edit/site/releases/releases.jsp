<%@page session="false" pageEncoding="UTF-8"%><%--
--%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<cpp:defineFrameObjects/>
<div class="panel panel-default releasesList">
    <div class="panel-heading" role="tab" id="releasesHead">
        <h4 class="panel-title">
            <a class="collapsed" role="button" data-toggle="collapse"
               href="#releasesPanel" aria-expanded="false" aria-controls="releasesPanel">
                Release List
            </a>
        </h4>
        <div class="btn-group" role="group" aria-label="...">
            <button type="button" class="btn btn-default public" data-path="${resource.path}">Public</button>
            <button type="button" class="btn btn-default preview" data-path="${resource.path}">Preview</button>
        </div>
        <div class="btn-group" role="group" aria-label="...">
            <button type="button" class="btn btn-default delete">Delete</button>
        </div>
    </div>
    <div id="releasesPanel" class="panel-collapse collapse in" role="tabpanel"
         aria-labelledby="releasesHead">
        <div class="panel-body">

<cpp:container var="site" type="com.composum.pages.commons.model.Site" mode="none">
    <table class="${siteCssBase}_table table">
        <thead class="${siteCssBase}_thead">
        <tr>
            <th class="${siteCssBase}_select"></th>
            <th class="${siteCssBase}_key">Key</th>
            <th class="${siteCssBase}_title">Title</th>
            <th class="${siteCssBase}_description">Description</th>
            <th class="${siteCssBase}_categories">Categories</th>
        </tr>
        </thead>
        <tbody class="${siteCssBase}_tbody">
        <c:forEach items="${site.releases}" var="release">
            <sling:include resource="${release.resource}" resourceType="composum/pages/stage/edit/site/releases/release"/>
        </c:forEach>
        </tbody>
    </table>
</cpp:container>

        </div>
    </div>
</div>
