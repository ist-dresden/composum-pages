<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:widget label="Link" property="link" type="linkfield"
            hint="the path of the links traget page or an external link"/>
<cpp:widget label="Link Title" property="linkTitle" type="textfield" i18n="true"
            hint="the text for the link tooltip (optional)"/>
