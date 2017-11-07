<%@page session="false" pageEncoding="utf-8"%><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %><%--
--%><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%--
--%><cpp:defineObjects/>
<cpn:clientlib type="js" category="${pageModel.viewClientlibCategory}"/>
<cpn:clientlib type="js" test="${pageModel.editMode}" category="${pageModel.editClientlibCategory}"/>
