<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@ taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineFrameObjects/>
<cpn:component var="model" type="com.composum.pages.stage.model.edit.FrameAsset">
    <c:if test="${model.versionable}">
        <cpp:menuItem icon="sign-${model.checkedOut?'in':'out'}"
                      label="${cpn:i18n(slingRequest,model.checkedOut?'Checkin':'Checkout')}"
                      title="${cpn:i18n(slingRequest,model.checkedOut?'Checkin':'Checkout')} ${cpn:i18n(slingRequest,'the selected page')}"
                      action="window.composum.pages.actions.page.toggleCheckout"/>
    </c:if>
    <c:if test="${model.toggleLockAvailable}">
        <cpp:menuItem test="${model.locked}" icon="unlock"
                      label="${cpn:i18n(slingRequest,'Unlock')} (${cpn:i18n(slingRequest,'locked by')} ${model.lockOwner})"
                      title="${cpn:i18n(slingRequest,'Unlock the selected page')}(${cpn:i18n(slingRequest,'locked by')} ${model.lockOwner})"
                      action="window.composum.pages.actions.page.toggleLock"/>
        <cpp:menuItem test="${!model.locked}" icon="lock" label="${cpn:i18n(slingRequest,'Lock')}"
                      title="${cpn:i18n(slingRequest,'Lock the selected page')}"
                      action="window.composum.pages.actions.page.toggleLock"/>
    </c:if>
</cpn:component>
