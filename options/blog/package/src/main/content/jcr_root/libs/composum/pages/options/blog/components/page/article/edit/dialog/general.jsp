<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<div class="row" style="align-items: flex-start;">
    <div class="col col-xs-9">
        <cpp:include resourceType="composum/pages/options/blog/components/static/intro/edit/dialog"
                     replaceSelectors="intro"/>
    </div>
    <div class="col col-xs-3">
        <cpp:widget label="Category" property="category" type="textfield" multi="true"/>
        <cpp:widget type="static" i18n="true" level="remark"
                    value="a set of short keywords in the site context; used for searching and filtering and embedded as 'keywords' in the pages meta data"/>
    </div>
</div>
