<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<div class="btn-group btn-group-sm" role="group">
    <cpp:treeMenu key="more" icon="navicon" label="More..." title="more page manipulation actions...">
        <cpp:menuItem icon="id-badge" label="Rename" title="rename the selected page"
                      action="window.composum.pages.actions.page.rename"/>
        <cpp:menuItem icon="arrows-alt" label="Move" title="move the selected page in the page hierarchy"
                      action="window.composum.pages.actions.page.move"/>
        <cpp:menuItem icon="sign-in" label="Checkout/Checkin" title="chekout/checkin the selected page (toggle checkout)"
                      action="window.composum.pages.actions.page.toggleCheckout"/>
        <cpp:menuItem icon="lock" label="Lock/Unlock" title="lock/unlock the selected page (toggle lock)"
                      action="window.composum.pages.actions.page.toggleLock"/>
    </cpp:treeMenu>
</div>
<cpp:treeAction icon="edit" label="Edit" title="Edit the page properties"
                action="window.composum.pages.actions.page.edit"/>
