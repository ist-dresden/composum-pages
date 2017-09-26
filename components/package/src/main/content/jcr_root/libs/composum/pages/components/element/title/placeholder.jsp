<%@page session="false" pageEncoding="UTF-8" %><%--
--%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %><%--
--%><%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%--
--%><cpp:defineObjects/>
<cpp:element var="element" type="com.composum.pages.commons.model.Element"
                test="@{element.editMode}" cssBase="composum-pages-components-placeholder">

    <span class="fa-stack fa-3x ${elementCssBase}_icon">
        <i class="fa fa-header fa-stack-2x"></i>
        <i class="fa fa-plus fa-stack-1x add-plus"></i>
    </span>

</cpp:element>
