<%@page session="false" pageEncoding="utf-8" %><%--
--%><%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><cpp:defineFrameObjects/>
<div id="${dialogTab.dialogId}_tab_${dialogTab.tabId}" class="${dialogCssBase}_tab_${dialogTab.tabId} ${dialogCssBase}_tab tab-pane" data-label="${cpn:text(dialogTab.label)}">
