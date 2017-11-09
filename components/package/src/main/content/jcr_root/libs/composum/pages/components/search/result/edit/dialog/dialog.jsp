<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<cpp:editDialog var="result" type="com.composum.pages.components.model.search.SearchField"
                title="Edit Search Result">
    <div class="row">
        <div class="col-lg-8 col-md-8 col-sm-8 col-xs-8">
            <cpp:widget label="Selector" property="selector" type="text" i18n="false" rules="optional"
                        hint="Sling selector which renders a found resource as search result. Default &quot;searchItem&quot;."/>
        </div>
        <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
            <cpp:widget label="Page size" property="pagesize" type="text" i18n="false" rules="optional"
                        hint="Page size for the shown results."/>
        </div>
    </div>
    <div class="row">
        <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
            <cpp:widget label="Search Root" property="searchRoot" type="path" i18n="false" rules="optional"
                        hint="Path where the search starts. Default: site-root."/>
            <cpp:widget label="Template" property="template" type="text" i18n="false" rules="optional"
                        hint="Template for printing the search results. Takes precedence over selector."/>
        </div>
    </div>
    <div class="row">
        <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
            <cpp:widget label="Headline" property="headline" type="richtext" i18n="true" rules="optional" height="55px"
                        hint="The head for the search result used as {@link MessageFormat} with the search expression used as argument {0}."/>
            <cpp:widget label="Error text" property="searchtermErrorText" type="richtext" i18n="true" rules="optional" height="100px"
                        hint="A text to show when the user inputs faulty search terms. Describing the syntax is recommended."/>
        </div>
    </div>
</cpp:editDialog>
