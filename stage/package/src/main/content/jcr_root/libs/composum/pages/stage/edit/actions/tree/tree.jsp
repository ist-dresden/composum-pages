<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<button type="button"
        class="fa fa-${action.icon} ${treeCssBase}_${action.icon} composum-pages-tools_button btn btn-default"
        title="${action.title}" data-action="${action.action}" ${action.attributes}><span
        class="composum-pages-tools_button-label">${action.label}</span></button>
