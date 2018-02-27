<%@page session="false" pageEncoding="utf-8" %><%--
--%><%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><cpp:defineFrameObjects/>
<cpp:treeAction icon="plus" label="Create" title="Create a new page"
                action="window.composum.pages.actions.page.create"/>
<cpp:treeAction icon="copy" label="Copy" title="Copy the selected page"
                action="window.composum.pages.actions.page.copy"/>
<cpp:treeAction icon="paste" label="Paste" title="Paste page as subpage of the selected page"
                action="window.composum.pages.actions.page.paste"/>
<cpp:treeAction icon="trash" label="Delete" title="Delete the selected page"
                action="window.composum.pages.actions.page.delete"/>
