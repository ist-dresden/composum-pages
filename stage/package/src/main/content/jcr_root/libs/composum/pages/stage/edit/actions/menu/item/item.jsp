<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<li><a class="${toolbarCssBase}_${action.icon}" href="#" title="${action.title}"
       data-action="${action.action}" ${action.attributes}><i class="fa fa-${action.icon}"></i>${action.label}</a></li>
