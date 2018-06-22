<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog var="decorator" type="com.composum.pages.components.model.decorator.Decorator"
                title="@{dialog.selector=='create'?'Create a Decorator':'Edit Decorator'}">
    <cpp:widget label="Content Type" property="elementType" type="textfield"
                hint="the default resource type of the embedded element"/>
    <div class="row">
        <div class="col-xs-6">
            <cpp:widget label="Warning Level" property="level" type="select" options="default,info,success,warning,danger"/>
        </div>
        <div class="col-xs-6">
            <cpp:widget label="Symbol" property="icon" type="select"
                        options=",at,asterisk,bars,bell-o:bell,bolt,bookmark-o:bookmark,bug,bullseye,calendar,certificate,check,comment,exclamation,eye,fire,gift,graduation-cap,info-circle:info,life-ring,lightbulb-o:lightbulb,lock,magic,map-marker,map-pin,pencil,picture-p:picture,power-off,question-circle-o:qestion,search,sliders,tag,times,thumb-o-down:thumbs-down,thumb-o-up:thumbs-up,warning,wrench"/>
        </div>
    </div>
</cpp:editDialog>
