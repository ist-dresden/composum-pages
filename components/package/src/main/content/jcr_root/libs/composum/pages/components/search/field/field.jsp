<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<cpp:defineObjects/>
<cpp:element var="field" type="com.composum.pages.components.model.search.SearchField">
    <cpn:form role="search" action="${field.searchResultLink}" method="GET">
        <input name="_charset_" type="hidden" value="UTF-8" class="${fieldCSS}_hidden"/>
        <div class="${fieldCSS}_group input-group" title="${cpn:text(field.hint)}">
            <input type="text" name="search.term" class="${fieldCSS}_input form-control"
                   placeholder="${cpn:text(field.placeholderText)}" value="${cpn:text(field.searchTerm)}"
                   aria-labelledby="searchbutton" role="searchbox">
            <span class="${fieldCSS}_button input-group-btn">
                <button type="submit" class="btn btn-default fa fa-${cpn:text(field.buttonSymbol)}"
                        id="searchbutton" aria-label="${cpn:i18n(slingRequest,'Search')}"><span
                        class="button-text">${cpn:text(field.buttonText)}</span></button>
            </span>
        </div>
    </cpn:form>
</cpp:element>
