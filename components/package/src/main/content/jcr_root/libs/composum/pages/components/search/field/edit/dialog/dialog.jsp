<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<cpp:editDialog var="field" type="com.composum.pages.components.model.search.SearchField"
                title="Edit Search Field">
    <cpp:editDialogGroup label="Input Field" expanded="true">
        <div class="row">
            <div class="col-lg-6 col-md-6 col-sm-6 col-xs-6">
                <cpp:widget label="Button Symbol" property="buttonSymbol" type="select" i18n="false"
                            options="search,question,filter,eye,bullseye" rules="optional"
                            hint="Symbol for the search button; optional, default: 'search'."/>
            </div>
            <div class="col-lg-6 col-md-6 col-sm-6 col-xs-6">
                <cpp:widget label="Button Text" property="buttonText" type="text" i18n="true" rules="optional"
                            hint="Text for the search button (normally not visible); optional."/>
            </div>
        </div>
        <div class="row">
            <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
                <cpp:widget label="Button Title" property="hint" type="text" i18n="true" rules="optional"
                            hint="Title (i.e., mouseover text) for the button; optional."/>
                <cpp:widget label="Placeholder Text" property="placeholderText" type="text" i18n="true" rules="optional"
                            hint="Text for the placeholder that is shown in the search field before any text is put in; optional."/>
            </div>
        </div>
    </cpp:editDialogGroup>
    <cpp:editDialogGroup label="Search Result" expanded="true">
        <div class="row">
            <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
                <cpp:widget label="Search Result Path" property="searchResultPath" type="path" i18n="false" rules="optional"
                            hint="Page in which the search result is shown; optional, default: containing page."/>
                <cpp:widget label="Search Result Anchor" property="searchResultAnchor" type="text" i18n="false" rules="optional"
                            hint="Anchor in the search result page to jump to for displaying the search result; optional."/>
            </div>
        </div>
    </cpp:editDialogGroup>
</cpp:editDialog>
