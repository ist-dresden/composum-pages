<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<cpp:defineFrameObjects/>
<div class="composum-pages-tools_search-panel">
    <div class="composum-pages-tools_search-form">
        <input class="composum-pages-tools_search-field form-control" type="text"
               title="${cpn:i18n(slingRequest,'Search Term')}"
               placeholder="${cpn:i18n(slingRequest,'search...')}"/>
        <select class="composum-pages-tools_search-scope form-control"
                title="${cpn:i18n(slingRequest,'Search Scope')}">
            <option value="site" selected="selected">${cpn:i18n(slingRequest,'Site')}</option>
            <option value="path">${cpn:i18n(slingRequest,'Current Path')}</option>
            <option value="content">${cpn:i18n(slingRequest,'Content')}</option>
        </select>
    </div>
    <div class="composum-pages-tools_search-result">
    </div>
    <div class="composum-pages-tools_search-searching">
        <i class="fa fa-spinner fa-pulse fa-5x fa-fw"></i>
    </div>
</div>

