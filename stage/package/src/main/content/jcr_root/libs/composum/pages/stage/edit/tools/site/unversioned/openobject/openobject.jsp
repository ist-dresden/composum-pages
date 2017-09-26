<%@page session="false" pageEncoding="UTF-8" %><%--
--%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><cpp:defineFrameObjects/>
<cpp:element var="openpage" type="com.composum.pages.commons.model.Page" mode="none"
               cssBase="composum-pages-stage-edit-tools-site-unversioned">
    <li class="${openpageCssBase}_listentry">
        <input type="checkbox" class="${openpageCssBase}_select" name="${openpageCssBase}_select"
               data-path="${openpage.path}"/>
        <div class="${openpageCssBase}_entry" data-path="${openpage.path}">
            <div class="${openpageCssBase}_head">
                <span class="${openpageCssBase}_title">${openpage.title}</span>
                <span class="${openpageCssBase}_time">${openpage.lastModifiedString}</span>
            </div>
            <div class="${openpageCssBase}_path">${openpage.siteRelativePath}</div>
        </div>
    </li>
</cpp:element>
