<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:widget label="Link" property="link" type="linkfield"
            hint="the path of the links traget page or an external link"/>
<div class="row">
    <div class="col col-xs-8">
        <cpp:widget label="Link Title" property="linkTitle" type="textfield" i18n="true"
                    hint="the text for the link tooltip (optional)"/>
    </div>
    <div class="col col-xs-4">
        <cpp:widget label="Link Target" property="target" type="combobox" options="_blank,_parent,_top,_self"/>
    </div>
</div>
