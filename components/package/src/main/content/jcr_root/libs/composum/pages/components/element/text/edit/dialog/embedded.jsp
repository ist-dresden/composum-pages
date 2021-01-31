<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<sling:call script="title.jsp"/>
<cpp:widget label="Text" property="text" type="richtext" i18n="true"/>
<sling:call script="alignment.jsp"/>
