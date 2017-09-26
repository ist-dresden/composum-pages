<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<cpp:editDialog var="field" type="com.composum.pages.components.model.search.SearchField"
                title="Edit Search Field">

    <cpp:widget label="Button Text" property="buttonText" type="text" i18n="true" rules="optional"
                hint="Text for the search button. Alternative to buttonSymbol and buttonImage."/>

    <cpp:widget label="Button Symbol" property="buttonSymbol" type="text" i18n="false" rules="optional"
                hint="Symbol for the search button as CSS class - string:CSS or path:Image."/>

    <cpp:widget label="Button Image" property="buttonImage" type="path" i18n="false" rules="optional"
                hint="Path to an image to be used for the search button. Alternative to buttonSymbol or buttonText."/>

    <cpp:widget label="Button Title" property="hint" type="text" i18n="true" rules="optional"
                hint="Title (i.e., mouseover text) for the button."/>

    <cpp:widget label="Placeholder Text" property="placeholderText" type="text" i18n="true" rules="optional"
                hint="Text for the placeholder that is shown in the search field before any text is put in."/>

    <cpp:widget label="Search Result Path" property="searchResultPath" type="path" i18n="false" rules="optional"
                hint="Page in which the search result is shown. path:Page. Optional; default: containing page."/>

    <cpp:widget label="Search Result Anchor" property="searchResultAnchor" type="text" i18n="false" rules="optional"
                hint="Anchor in the search result page to jump to for displaying the search result. string:Anchor, optional."/>

</cpp:editDialog>
