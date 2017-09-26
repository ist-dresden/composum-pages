<%@page session="false" pageEncoding="UTF-8"%><%--
--%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<cpp:defineFrameObjects/>
<cpp:container var="languages" type="com.composum.pages.stage.model.edit.site.Languages" mode="edit">
    <table class="${languagesCssBase}_table table">
        <thead class="${languagesCssBase}_thead">
        <tr>
            <th class="${languagesCssBase}_name">Name</th>
            <th class="${languagesCssBase}_key">Key</th>
            <th class="${languagesCssBase}_label">Label</th>
            <th class="${languagesCssBase}_direction">Direction</th>
        </tr>
        </thead>
        <tbody class="${languagesCssBase}_tbody">
        <c:forEach items="${languages.languageList}" var="language">
            <sling:include resource="${language}" resourceType="composum/pages/stage/edit/site/languages/language"/>
        </c:forEach>
        </tbody>
    </table>
</cpp:container>
