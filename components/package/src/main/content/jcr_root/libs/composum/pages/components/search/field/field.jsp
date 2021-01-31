<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<cpp:defineObjects/>
<cpp:element var="model" type="com.composum.pages.components.model.search.SearchField">
    <cpn:form class="${modelCSS}_form" role="search" action="${model.searchResultLink}" method="GET">
        <cpn:text tagName="${model.titleTagName}" class="${modelCSS}_title" value="${model.title}"/>
        <input name="_charset_" type="hidden" value="UTF-8" class="${modelCSS}_hidden"/>
        <cpn:div tagName="input" test="${not empty model.linkLocaleParam}" type="hidden"
                 name="pages.locale" value="${model.linkLocaleParam}"/>
        <div class="${modelCSS}_group input-group" title="${cpn:text(model.hint)}">
            <input type="text" name="search.term" class="${modelCSS}_input form-control"
                   placeholder="${cpn:text(model.placeholderText)}" value="${cpn:value(model.searchTerm)}"
                   aria-labelledby="searchbutton" role="searchbox">
            <span class="${modelCSS}_button input-group-btn">
                <button type="submit" class="btn btn-default fa fa-${cpn:text(model.buttonSymbol)}"
                        id="searchbutton" aria-label="${cpn:i18n(slingRequest,'Search')}"><span
                        class="button-text">${cpn:text(model.buttonText)}</span></button>
            </span>
        </div>
    </cpn:form>
</cpp:element>
