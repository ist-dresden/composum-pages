<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<cpn:text tagName="title" value="${currentPage.title}"/>
<meta name="keywords" content="${cpn:text(currentPage.keywords)}"/>
<meta name="description" content="${cpn:rich(slingRequest,currentPage.description)}"/>
<meta name="viewport"
      content="width=device-width, minimum-scale=1, maximum-scale=1, user-scalable=no, viewport-fit=cover"/>
<meta name="format-detection" content="telephone=no"/>
