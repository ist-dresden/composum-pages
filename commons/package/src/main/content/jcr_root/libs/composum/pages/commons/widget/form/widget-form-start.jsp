<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineObjects/>
<div${form.attributes}>
    <form action="${form.formAction.url}" method="${form.formAction.method}"
    <c:if test="${form.postMethod}"> encType="${form.formAction.encType}"</c:if>
          class="${formCSS}_form form-widget">
        <!-- start of form content -->
