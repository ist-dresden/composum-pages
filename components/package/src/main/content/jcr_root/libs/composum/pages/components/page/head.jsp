<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<sling:call script="meta.jsp"/>
<sling:call script="hreflang.jsp"/>
<cpn:clientlib type="link" category="${pageModel.viewClientlibCategory}"/>
<cpn:clientlib type="css" category="${pageModel.viewClientlibCategory}"/>
<cpn:clientlib type="css" test="${pageModel.editMode}" category="${pageModel.editClientlibCategory}"/>
