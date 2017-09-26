<%@page session="false" pageEncoding="UTF-8" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><cpp:defineObjects/>
<cpp:element var="code" type="com.composum.pages.components.model.codeblock.CodeBlock" cssAdd="@{code.classes}"
             data-languagetype="@{code.codeLanguage}">
    <cpp:include replaceSelectors="${code.renderType}"/>
</cpp:element>
