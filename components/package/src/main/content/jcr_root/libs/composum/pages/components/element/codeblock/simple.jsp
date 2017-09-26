<%@page session="false" pageEncoding="UTF-8" %><%--
--%><%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%--
--%><cpp:defineObjects/>
<cpp:element var="code" type="com.composum.pages.components.model.codeblock.CodeBlock" tagName="none">
    <c:choose>
        <c:when test="${code.valid}">
            <div class="${codeCssBase}_content-block">
                <pre class="${codeCssBase}_content"><code
                        class="${code.codeLanguage}"><cpn:text value="${code.code}"/></code></pre>
            </div>
            <c:if test="${code.hasCopyright}">
                <cpn:text tagClass="${codeCssBase}_footer simple-footer" value="${code.copyright}"/>
            </c:if>
        </c:when>
        <c:otherwise>
            <cpp:include replaceSelectors="placeholder"/>
        </c:otherwise>
    </c:choose>
</cpp:element>
