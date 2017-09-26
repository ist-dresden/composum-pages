<%@page session="false" pageEncoding="UTF-8"%>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<cpp:defineFrameObjects/>


<div class="panel panel-default finishedPages">
    <div class="panel-heading" role="tab" id="finishedPagesHead">
        <h4 class="panel-title">
            <a class="collapsed" role="button" data-toggle="collapse"
               href="#finishedPagesPanel" aria-expanded="false"
               aria-controls="finishedPagesPanel">
                Finished Objects (version after last release)
            </a>
        </h4>
        <div class="btn-group" role="group" aria-label="...">
            <button type="button" class="btn btn-default release" data-path="${resource.path}">Do Release...</button>
        </div>
    </div>
    <div id="finishedPagesPanel" class="panel-collapse collapse in" role="tabpanel" aria-labelledby="finishedPagesHead">
        <div class="panel-body">

            <cpp:container var="finishedObjects" type="com.composum.pages.stage.model.edit.site.FinishedObjects" mode="none">
                <table class="${finishedObjectsCssBase}_table table">
                    <thead class="${finishedObjectsCssBase}_thead">
                        <tr>
                            <th><input type="checkbox" class="${finishedObjectsCssBase}_select" name="${finishedObjectsCssBase}_select" /></th>
                            <th class="${finishedObjectsCssBase}_key">rel. Path</th>
                            <th class="${finishedObjectsCssBase}_title">Title</th>
                            <th class="${finishedObjectsCssBase}_description">modification Date</th>
                        </tr>
                    </thead>
                    <tbody class="${finishedObjectsCssBase}_tbody">
                        <c:forEach items="${finishedObjects.objectList}" var="finishedObject">
                             <sling:include resource="${finishedObject}" resourceType="composum/pages/stage/edit/site/finishedobjects/finishedobject"/>
                        </c:forEach>
                    </tbody>
                </table>
            </cpp:container>

        </div>
    </div>
</div>
