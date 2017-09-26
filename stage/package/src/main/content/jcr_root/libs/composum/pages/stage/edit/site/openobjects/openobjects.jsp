<%@page session="false" pageEncoding="UTF-8"%>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

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

        <cpp:container var="openObjects" type="com.composum.pages.stage.model.edit.site.OpenObjects" mode="none">
            <table class="${openObjectsCssBase}_table table">
                <thead class="${openObjectsCssBase}_thead">
                <tr>
                    <th class="${openObjectsCssBase}_key">rel. Path</th>
                    <th class="${openObjectsCssBase}_title">Title</th>
                    <th class="${openObjectsCssBase}_description">modification Date</th>
                </tr>
                </thead>
                <tbody class="${openObjectsCssBase}_tbody">
                <c:forEach items="${openObjects.objectList}" var="openObject">
                     <sling:include resource="${openObject}" resourceType="composum/pages/stage/edit/site/openobjects/openobject"/>
                </c:forEach>
                </tbody>
            </table>
        </cpp:container>

        </div>
    </div>
</div>
