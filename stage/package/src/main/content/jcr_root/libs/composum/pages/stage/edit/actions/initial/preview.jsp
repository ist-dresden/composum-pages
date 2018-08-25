<%@page session="false" pageEncoding="utf-8" %><%--
--%><%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><cpp:defineFrameObjects/>
<cpp:editToolbar var="frame" type="com.composum.pages.stage.model.edit.FramePage">
    <a class="fa fa-sitemap ${toolbarCssBase}_favorite ${toolbarCssBase}_button" href="?pages.mode=browse"
       title="${cpn:i18n(slingRequest,'Use Browse Mode')}"><span
            class="${toolbarCssBase}_label">${cpn:i18n(slingRequest,'Use Browse Mode')}</span></a>
</cpp:editToolbar>
