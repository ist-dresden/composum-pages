<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editToolbar>
    <%--
    <cpp:editAction icon="star-o" label="Toggle Favorite" title="Toggle Favorite"
                    action="window.composum.pages.actions.page.favorite"/>
    --%>
    <cpp:editAction icon="edit" label="Edit" title="Edit the page properties"
                    action="window.composum.pages.actions.page.edit"/>
    <cpp:editAction icon="copy" label="Copy" title="Copy this page"
                    action="window.composum.pages.actions.page.copy"/>
    <cpp:editAction icon="trash" label="Delete" title="Delete this page!"
                    action="window.composum.pages.actions.page.delete"/>
</cpp:editToolbar>
