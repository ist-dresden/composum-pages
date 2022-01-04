<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<cpp:defineObjects/>
<cpn:clientlib type="js" category="${currentPage.viewClientlibCategory}"/>
<c:if test="${!currentPage.editMode}"><sling:call script="ready.jsp"/></c:if>
<cpn:clientlib type="js" test="${currentPage.editMode}" category="${currentPage.editClientlibCategory}"/>
