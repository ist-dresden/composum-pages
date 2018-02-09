<%@page session="false" pageEncoding="UTF-8"%><%--
--%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%--
--%><cpp:defineFrameObjects/>
<cpp:element var="languages" type="com.composum.pages.stage.model.edit.site.Languages" mode="none">
    <label>Languages</label>
    <ul class="${languagesCssBase}_list">
        <c:forEach items="${languages.languageList}" var="language">
            <sling:include resource="${language}" resourceType="composum/pages/stage/edit/tools/site/releases/language"/>
        </c:forEach>
    </ul>
</cpp:element>
