<%@page session="false" pageEncoding="UTF-8" %><%--
--%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><cpp:defineFrameObjects/>
<cpp:element var="finished" type="com.composum.pages.commons.model.Page" mode="none"
               cssBase="composum-pages-stage-edit-tools-site-unreleased-finishedobject">
    <li class="${finishedCssBase}_listentry">
        <input type="checkbox" class="${finishedCssBase}_select" name="${finishedCssBase}_select"
               data-path="${finished.path}"/>
        <div class="${finishedCssBase}_entry" data-path="${finished.path}">
            <div class="${finishedCssBase}_head">
                <span class="${finishedCssBase}_title">${finished.title}</span>
                <span class="${finishedCssBase}_time">${finished.lastModifiedString}</span>
            </div>
            <div class="${finishedCssBase}_path">${finished.siteRelativePath}</div>
        </div>
    </li>
</cpp:element>
