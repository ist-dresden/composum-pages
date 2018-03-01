<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:treeMenu key="more" icon="navicon" label="More..." title="more page manipulation actions...">
    <cpp:menuItem icon="" label="Rename" title="rename the selected page"
                  action="window.composum.pages.actions.page.rename"/>
    <cpp:menuItem icon="" label="Move" title="move the selected page in the page hierarchy"
                  action="window.composum.pages.actions.page.move"/>
    <cpp:menuItem icon="" label="Checkout/Checkin" title="chekout/checkin the selected page (toggle checkout)"
                  action="window.composum.pages.actions.page.checkout"/>
    <cpp:menuItem icon="" label="Lock/Unlock" title="lock/unlock the selected page (toggle lock)"
                  action="window.composum.pages.actions.page.lock"/>
</cpp:treeMenu>
<cpp:treeAction icon="edit" label="Edit" title="Edit the page properties"
                action="window.composum.pages.actions.page.edit"/>
