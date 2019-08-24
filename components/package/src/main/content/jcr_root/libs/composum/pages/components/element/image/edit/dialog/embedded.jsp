<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:widget label="Image" property="imageRef" type="imagefield"
            hint="the path to the image asset in the repository or an URL"/>
<cpp:widget label="Title" property="title" type="textfield" i18n="true"
            hint="the title text normally shown as the elements tooltip"/>
<cpp:widget label="Alt Text" property="alt" type="textfield" i18n="true"
            hint="the text used as alternative content in text based view"/>
<sling:call script="meta.jsp"/>
