<%@page session="false" pageEncoding="utf-8" contentType="text/plain" %><%
%><%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %><%
%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%
%><cpp:defineObjects/><%
%><cpp:model var="sitemap" type="com.composum.pages.components.model.page.Sitemap"><%
%>${cpn:text(sitemap.robotsTxt)}<%
%></cpp:model>