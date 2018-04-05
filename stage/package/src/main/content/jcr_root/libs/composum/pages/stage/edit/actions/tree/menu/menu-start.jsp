<%@page session="false" pageEncoding="utf-8" %><%--
--%><%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><cpp:defineFrameObjects/>
<button type="button" class="fa fa-${menu.icon} composum-pages-tools_button btn btn-default dropdown dropdown-toggle"
        data-toggle="dropdown" href="#" title="${menu.title}" aria-haspopup="true" aria-expanded="false"><span
        class="composum-pages-tools_button-label">${menu.label}</span></button>
<ul class="${toolbarCssBase}_${menu.key} composum-pages-tools_menu dropdown-menu" role="menu">
