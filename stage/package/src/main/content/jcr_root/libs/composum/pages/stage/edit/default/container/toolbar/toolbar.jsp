<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editToolbar cssBase="composum-pages-stage-edit-toolbar">
    <cpp:editAction icon="plus" label="Insert Element" title="Insert a new Element"
                    action="window.composum.pages.actions.container.insert"/>
    <cpp:editAction icon="paste" label="Paste copied Element" title="Insert a copy of an Element from the clipboard"
                    action="window.composum.pages.actions.container.paste"/>
    <cpp:editAction icon="copy" label="Copy" title="Copy the selected container"
                    action="window.composum.pages.actions.element.copy"/>
    <cpp:editAction icon="trash" label="Delete" title="Delete the selected Container"
                    action="window.composum.pages.actions.element.delete"/>
</cpp:editToolbar>
