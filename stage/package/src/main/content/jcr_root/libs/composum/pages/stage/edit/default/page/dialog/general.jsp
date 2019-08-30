<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<div class="row">
    <div class="col col-xs-6">
        <cpp:widget label="Title" property="jcr:title" type="textfield" i18n="true"
                    hint="the page title / headline"/>
    </div>
    <div class="col col-xs-6">
        <cpp:widget label="Subtitle" property="subtitle" type="textfield" i18n="true"
                    hint="the optional subtitle / slogan"/>
    </div>
</div>
<cpp:widget label="Description" property="jcr:description" type="richtext" height="150" i18n="true"
            hint="a short abstract / teaser text of the page"/>
