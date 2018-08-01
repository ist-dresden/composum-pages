<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:model var="pagemodel" type="com.composum.pages.commons.model.Page">
    <cpp:widget label="Page Languages" property="pageLanguages" type="multicheck" i18n="false"
                options="${pagemodel.languages}"/>
</cpp:model>
