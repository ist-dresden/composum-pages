<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpn:bundle basename="composum-pages">
    <div class="composum-pages-stage-edit-dialogs composum-widget">
    </div>
    <div class="composum-pages-stage-widget-dialogs composum-widget">
        <sling:call script="widgets-dialogs.jsp"/>
    </div>
</cpn:bundle>