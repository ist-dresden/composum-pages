<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog title="@{dialog.selector=='create'?'Create a Section':'Section Properties'}">
    <div class="row">
        <div class="col col-xs-7">
            <cpp:widget label="Title" property="jcr:title" type="textfield" i18n="true"
                        hint="an optional title as section header"/>
        </div>
        <div class="col col-xs-3">
            <cpp:widget label="Anchor" property="anchor" type="textfield"
                        hint="optional navigation id"/>
        </div>
        <div class="col col-xs-2">
            <cpp:widget label="Hide Title" property="hideTitle" type="checkbox"/>
        </div>
    </div>
    <cpp:widget type="static"
                value="A section can be decorated by a symbol and a 'warning' level to produce more attention for the sections content:"/>
    <div class="row">
        <div class="col col-xs-6">
            <cpp:widget label="Warning Level" property="level" type="select"
                        hint="<a href='https://getbootstrap.com/docs/3.3/components/#panels-alternatives' target='_blank'>'Bootstrap' background</a>"
                        options=":none,default,info,success,warning,danger"/>
        </div>
        <div class="col col-xs-6">
            <cpp:widget label="Symbol" property="icon" type="iconcombobox"
                        hint="<a href='https://fontawesome.com/v4.7.0/icons/' target='_blank'>'FontAwesome'</a> icon key"
                        options="at,asterisk,bookmark-o:bookmark,check,exclamation,eye,info-circle:info,lightbulb-o:lightbulb,question-circle-o:qestion,search,warning,wrench"
                        typeahead="/bin/cpm/core/system.typeahead.json/libs/fonts/awesome/4.7.0/font-awesome-keys.txt"/>
        </div>
    </div>
</cpp:editDialog>
