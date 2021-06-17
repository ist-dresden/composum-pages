<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:widget label="Title" property="jcr:title" type="textfield" i18n="true" required="true"
            hint="the page title / headline"/>
<cpp:widget label="Subtitle" property="subtitle" type="textfield" i18n="true"
            hint="the optional subtitle / slogan"/>
<cpp:widget label="Intro Text" property="jcr:description" type="richtext" height="300" i18n="true"
            hint="the Blog / Article intro text shown on top of the blog page"/>