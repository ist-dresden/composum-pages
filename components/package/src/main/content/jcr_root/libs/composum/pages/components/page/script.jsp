<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<cpp:defineObjects/>
<cpn:clientlib type="js" category="${currentPage.viewClientlibCategory}"/>
<cpn:clientlib type="js" test="${currentPage.editMode}" category="${currentPage.editClientlibCategory}"/>
