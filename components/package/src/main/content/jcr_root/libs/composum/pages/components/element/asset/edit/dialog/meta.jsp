<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<div class="row">
    <div class="col col-xs-6">
        <cpp:widget label="Copyright" property="copyright" type="textfield" i18n="true"
                    hint="a copyright information text"/>
    </div>
    <div class="col col-xs-6">
        <cpp:widget label="Copyright URL" property="copyrightUrl" type="linkfield" i18n="true"
                    hint="the copyright holders URL"/>
    </div>
</div>
<div class="row">
    <div class="col col-xs-6">
        <cpp:widget label="License" property="license" type="textfield" i18n="true"
                    hint="the label of the link to the license"/>
    </div>
    <div class="col col-xs-6">
        <cpp:widget label="License URL" property="licenseUrl" type="linkfield" i18n="true"
                    hint="the URL of the license"/>
    </div>
</div>
