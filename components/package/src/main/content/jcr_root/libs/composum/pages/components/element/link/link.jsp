<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<cpp:element var="link" type="com.composum.pages.commons.model.Link"
             test="@{link.valid||link.editMode}">
    <cpp:dropZone property="link" filter="page:site;asset:document">
        <cpn:link class="${linkCSS}_link" href="${link.linkUrl}" target="${link.linkTarget}"
                  title="${link.linkTitle}">${cpn:text(link.title)}</cpn:link>
    </cpp:dropZone>
</cpp:element>
