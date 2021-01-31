<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<cpp:element var="model" type="com.composum.pages.components.model.map.HereMap">
    <div class="${modelCSS}_wrapper">
        <cpn:link href="${model.linkUrl}" test="${not empty model.linkUrl}" class="${modelCSS}_link" body="true">
            <img class="${modelCSS}_image" src="${model.mapviewUrl}" alt="${model.linkUrl}"/>
        </cpn:link>
    </div>
</cpp:element>
