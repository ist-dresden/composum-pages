<%@page session="false" pageEncoding="utf-8" %><%--
--%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %><%--
--%><%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><cpp:defineFrameObjects/>
<cpp:editDialog var="carousel" type="com.composum.pages.components.model.container.Carousel"
                title="@{dialog.selector=='create'?'Create a Carousel':'Edit Carousel'}">
    <div class="row">
        <div class="col-xs-4">
            <cpp:widget label="Use controls" property="useControls" type="checkbox"/>
        </div>
        <div class="col-xs-4">
            <cpp:widget label="Show indicators" property="showIndicators" type="checkbox"/>
        </div>
        <div class="col-xs-4">
            <cpp:widget label="Auto start" property="autoStart" type="checkbox"/>
        </div>
    </div>
    <div class="row">
        <div class="col-xs-4">
            <cpp:widget label="Interval" property="interval" type="textfield"/>
        </div>
        <div class="col-xs-8">
            <cpp:widget label="Mouse enter doesn't stop" property="noPause" type="checkbox"/>
        </div>
    </div>
</cpp:editDialog>
