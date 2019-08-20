<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<cpp:editDialog title="Edit Search Field">
    <cpp:editDialogGroup label="Input Field" expanded="true">
        <div class="row">
            <div class="col col-xs-6">
                <cpp:widget label="Button Text" property="buttonText" type="textfield" i18n="true"
                            hint="Text for the search button (normally not visible); optional."/>
            </div>
            <div class="col col-xs-6">
                <cpp:widget label="Button Title" property="hint" type="textfield" i18n="true"
                            hint="Title (i.e., mouseover text) for the button; optional."/>
            </div>
        </div>
        <div class="row">
            <div class="col col-xs-8">
                <cpp:widget label="Placeholder Text" property="placeholderText" type="textfield" i18n="true"
                            hint="Text for the placeholder that is shown in the search field before any text is put in; optional."/>
            </div>
            <div class="col col-xs-4">
                <cpp:widget label="Button Symbol" property="buttonSymbol" type="iconcombobox"
                            options="search,question,filter,eye,bullseye" default="search"
                            typeahead="/bin/cpm/core/system.typeahead.json/libs/fonts/awesome/4.7.0/font-awesome-keys.txt"
                            hint="Symbol for the search button; optional, default: 'search'."/>
            </div>
        </div>
    </cpp:editDialogGroup>
    <cpp:editDialogGroup label="Search Result" expanded="true">
        <cpp:widget label="Search Result Path" property="searchResultPath" type="pathfield" i18n="false"
                    hint="Page in which the search result is shown; optional, default: containing page."/>
        <cpp:widget label="Search Result Anchor" property="searchResultAnchor" type="textfield" i18n="false"
                    hint="Anchor in the search result page to jump to for displaying the search result; optional."/>
    </cpp:editDialogGroup>
</cpp:editDialog>
