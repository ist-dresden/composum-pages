<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineFrameObjects/>
<cpp:container var="languages" type="com.composum.pages.stage.model.edit.site.Languages" mode="none">
    <table class="${languagesCSS}_table table">
        <thead class="${languagesCSS}_thead">
        <tr>
            <th class="${languagesCSS}_name">Name</th>
            <th class="${languagesCSS}_key">Key</th>
            <th class="${languagesCSS}_label">Label</th>
            <th class="${languagesCSS}_direction">Direction</th>
        </tr>
        </thead>
        <tbody class="${languagesCSS}_tbody">
        <c:forEach items="${languages.languageList}" var="language">
            <cpp:include resource="${language}" resourceType="composum/pages/stage/edit/site/languages/language"/>
        </c:forEach>
        </tbody>
    </table>
</cpp:container>
