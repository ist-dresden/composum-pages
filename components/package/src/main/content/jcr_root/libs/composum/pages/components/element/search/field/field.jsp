<%@page session="false" pageEncoding="utf-8"%><%--
--%><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0"%><%--
--%><cpp:defineObjects />
<cpp:element var="field" type="com.composum.pages.components.model.search.SearchField">
    <cpn:form role="search" action="${field.searchResultLink}" method="GET">
        <div class="input-group" title="${cpn:text(field.hint)}">
            <input type="text" name="search.text" class="form-control" placeholder="${cpn:text(field.placeholderText)}"
                   aria-labelledby="searchbutton" role="searchbox">
            <span class="input-group-btn">
                <button type="submit" class="btn btn-default ${cpn:text(field.buttonSymbol)}"
                        id="searchbutton" aria-label="Search">
                    <c:choose>
                        <c:when test="${not empty field.buttonImage}"><cpn:image src="${field.buttonImage}"/></c:when>
                        <c:otherwise>${cpn:text(field.buttonText)}</c:otherwise>
                    </c:choose>
                </button>
            </span>
        </div>
    </cpn:form>
</cpp:element>
