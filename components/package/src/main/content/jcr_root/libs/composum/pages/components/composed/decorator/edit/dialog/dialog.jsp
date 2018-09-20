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
        <div class="col col-xs-6">
            <cpp:widget label="Warning Level" property="level" type="select"
                        hint="<a href='https://getbootstrap.com/docs/3.3/components/#panels-alternatives' target='_blank'>'Bootstrap' background</a>"
                        options="none,default,primary,info,success,warning,danger"/>
        </div>
        <div class="col col-xs-6">
            <cpp:widget label="Symbol" property="icon" type="iconcombobox"
                        hint="<a href='https://fontawesome.com/v4.7.0/icons/' target='_blank'>'FontAwesome'</a> icon key"
                        options="at,asterisk,bookmark-o:bookmark,check,exclamation,eye,info-circle:info,lightbulb-o:lightbulb,question-circle-o:qestion,search,warning,wrench"
                        typeahead="/bin/cpm/core/system.typeahead.json/libs/fonts/awesome/4.7.0/font-awesome-keys.txt"/>
        </div>
    </div>
</cpp:editDialog>
