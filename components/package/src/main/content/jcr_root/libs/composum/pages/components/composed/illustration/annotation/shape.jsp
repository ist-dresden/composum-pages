<%@page session="false" pageEncoding="UTF-8" %><%--
--%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %><%--
--%><%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="C" uri="http://java.sun.com/jsp/jstl/core" %><%--
--%><cpp:defineObjects/>
<cpp:element var="annotation" type="com.composum.pages.components.model.illustration.Annotation"
               cssSet="@{annotationCssBase}_shape type-@{annotation.shapeType} icon-@{annotation.iconType}"
               style="@{annotation.shapeStyle}" data-id="@{annotationId}">
    <i class="${annotation.iconClasses}">${annotation.shapeText}</i>
</cpp:element>
