<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:treeAction icon="upload" label="Upload" title="Upload the microsite content"
                action="window.composum.pages.actions.dialog.open" selectors="upload"/>
<cpp:treeAction icon="copy" label="Copy" title="Copy this microsite page and its content"
                action="window.composum.pages.actions.page.copy"/>
<cpp:treeAction icon="trash" label="Delete" title="Delete the entire microsite"
                action="window.composum.pages.actions.page.delete"/>
