<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<cpp:element var="map" type="com.composum.pages.components.model.map.GoogleMap">
    <div class="${mapCssBase}_wrapper">
        <iframe class="${mapCssBase}_frame" width="100%" height="100%" frameborder="0" src="${map.frameUrl}"></iframe>
    </div>
</cpp:element>
