<%@page session="false" pageEncoding="utf-8"
        import="com.composum.pages.commons.util.RequestUtil,
                org.apache.commons.lang3.StringUtils" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<sling:defineObjects/><%
    /*
     * forward dialog request to the page content instead of the 'virtual' teaser component, ensures
     * that the event properties can be edited as part of the page decorated as 'teaser' with a reload
     * of the page content after save
     */
    RequestUtil.forward(slingRequest, slingResponse, resource.getParent(), true,
            "composum/pages/components/time/event/edit/dialog", null,
            StringUtils.substringBeforeLast(slingRequest.getRequestPathInfo().getSuffix(), "/"));
%>
