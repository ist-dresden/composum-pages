<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<cpp:defineFrameObjects/>

<cpp:element var="language" type="com.composum.pages.stage.model.edit.site.Language" mode="none">
    <li class="${languageCssBase}_listentry">
        <div class="${languageCssBase}_entry" >
            <div class="${languageCssBase}_label">${language.label}</div>
        </div>
    </li>
</cpp:element>
