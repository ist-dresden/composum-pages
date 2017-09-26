<%@page session="false" pageEncoding="UTF-8" %><%--
--%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %><%--
--%><%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%--
--%><cpp:defineObjects/>
<cpp:property var="set" type="com.composum.pages.commons.model.properties.PropertyNodeSet"
              property="path/to/child/multitest3" i18n="true">
    <h4>${cpn:i18n(slingRequest, 'Multi Value Table')}</h4>
    <table class="table">
        <thead>
        <tr>
            <th><cpn:text value="Label" i18n="true"/></th>
            <th><cpn:text value="active" i18n="true"/></th>
            <th><cpn:text value="Type" i18n="true"/></th>
            <th><cpn:text value="Weight" i18n="true"/></th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${set}" var="item">
            <tr>
                <td><cpn:text value="${item.label}"/></td>
                <td><cpn:text value="${item.active?'active':'--'}"/></td>
                <td><cpn:text value="${item.type}"/></td>
                <td><cpn:text value="${item.weight}"/></td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</cpp:property>