<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineObjects/>
<cpp:model var="code" type="com.composum.pages.components.model.codeblock.CodeBlock">
    <c:choose>
        <c:when test="${code.valid}">
            <div class="${codeCSS}_panel panel panel-default">
                <c:if test="${code.showHeading}">
                    <div class="${codeCSS}_heading panel-heading">
                        <cpn:text tagName="span" class="${codeCSS}_title" value="${code.title}"/>
                        <c:if test="${code.showLanguage}">
                            <cpn:text tagName="span" class="${codeCSS}_language badge"
                                      value="${code.codeLanguage}"/>
                        </c:if>
                        <c:if test="${code.collapsible}">
                            <cpn:text tagName="button"
                                      class="${codeCSS}_button btn btn-xs btn-default ${codeCSS}_expand"
                                      value="expand code"/>
                            <cpn:text tagName="button"
                                      class="${codeCSS}_button btn btn-xs btn-default ${codeCSS}_collapse"
                                      value="collapse code"/>
                        </c:if>
                    </div>
                </c:if>
                <div class="${codeCSS}_content-block panel-body">
                <div class="${codeCSS}_content"><code
                        class="${code.codeLanguage}"><cpn:text>${code.code}</cpn:text></code></div>
                </div>
                <c:if test="${code.hasCopyright}">
                    <cpn:text class="${codeCSS}_footer panel-footer" value="${code.copyright}"/>
                </c:if>
            </div>
        </c:when>
        <c:otherwise>
            <cpp:include replaceSelectors="placeholder"/>
        </c:otherwise>
    </c:choose>
</cpp:model>
