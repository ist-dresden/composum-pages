<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:widget label="Location" property="location" type="textfield" i18n="false"
            hint="the location to mark on the map (address)"/>
<cpp:widget label="URL Parameters" property="parameters" type="textfield" i18n="false" multi="true"
            hint="additional parameters for the map request ('key=value')"/>
