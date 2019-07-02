<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<a class="fa fa-${action.icon} ${toolbarCssBase}_${action.icon} ${toolbarCssBase}_button" href="#"
       title="${action.title}" data-action="${action.action}" ${action.attributes}><span
            class="${toolbarCssBase}_label">${action.label}</span></a>
