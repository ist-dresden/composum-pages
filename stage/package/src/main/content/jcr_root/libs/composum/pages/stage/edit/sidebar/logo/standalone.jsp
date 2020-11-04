<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:element var="logo" type="com.composum.pages.commons.model.Element" mode="none">
    <div class="${logoCssBase}_text">
        <div class="${logoCssBase}_title">Composum</div>
        <div class="${logoCssBase}_subtitle">pages</div>
    </div>
    <cpn:div class="${logoCssBase}_standalone">
        <cpn:image class="${logoCssBase}_image"
                   src="/libs/composum/pages/stage/images/composum-blue-yellow-on-black.png"/>
    </cpn:div>
</cpp:element>
