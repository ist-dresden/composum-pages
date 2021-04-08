<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog var="model" type="com.composum.pages.options.wiki.model.Markup"
                title="Edit Wiki Markup">
    <div class="row">
        <div class="col-xs-9">
            <cpp:widget label="Title" property="jcr:title" name="title" type="textfield" i18n="true"
                        hint="an optional title text rendered on top of the markup tex"/>
        </div>
        <div class="col-xs-3">
            <cpp:widget label="Wiki Type" property="wikiType" type="select"
                        options="${model.wikiTypes}" default="confluence"/>
        </div>
    </div>
    <cpp:widget label="Markup Code" property="wikiCode" type="codearea" height="420" i18n="true"
                hint="the Wiki markup code according to the selected markup type"/>
    <cpp:widget label="Markup Resource" property="wikiRef" type="pathfield" i18n="true"
                hint="an alternative resource path to the markup file used if the code is empty"/>
</cpp:editDialog>
