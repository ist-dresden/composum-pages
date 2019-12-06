<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<div class="row" style="align-items: flex-start;">
    <div class="col col-xs-9">
        <div class="row">
            <div class="col col-xs-12">
                <cpp:widget label="Title" property="jcr:title" type="textfield" i18n="true"
                            hint="the page title / headline"/>
            </div>
        </div>
        <div class="row">
            <div class="col col-xs-12">
                <cpp:widget label="Subtitle" property="subtitle" type="textfield" i18n="true"
                            hint="the optional subtitle / slogan"/>
            </div>
        </div>
        <div class="row">
            <div class="col col-xs-12">
                <cpp:widget label="Description" property="jcr:description" type="richtext" height="150" i18n="true"
                            hint="a short abstract / teaser text of the page"/>
            </div>
        </div>
    </div>
    <div class="col col-xs-3">
        <cpp:widget label="Category" property="category" type="textfield" multi="true"/>
        <cpp:widget type="static" i18n="true" level="remark"
                    value="a set of short keywords in the site context; used for searching and filtering and embedded as 'keywords' in the pages meta data"/>
    </div>
</div>
