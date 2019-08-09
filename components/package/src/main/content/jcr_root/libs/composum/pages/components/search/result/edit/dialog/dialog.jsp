<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<cpp:editDialog title="Edit Search Result">
    <div class="row">
        <div class="col col-xs-8">
            <cpp:widget label="Selector" property="selector" type="textfield"
                        hint="Sling selector which renders a found resource as search result. Default &quot;searchItem&quot;."/>
        </div>
        <div class="col col-xs-4">
            <cpp:widget label="Page size" property="pagesize" type="textfield"
                        hint="Page size for the shown results."/>
        </div>
    </div>
    <div class="row">
        <div class="col col-xs-12">
            <cpp:widget label="Search Root" property="searchRoot" type="pathfield"
                        hint="Path where the search starts. Default: site-root."/>
            <cpp:widget label="Template" property="template" type="textfield"
                        hint="Template for printing the search results. Takes precedence over selector."/>
        </div>
    </div>
    <div class="row">
        <div class="col col-xs-12">
            <cpp:widget label="Headline" property="headline" type="richtext" i18n="true" height="55px"
                        hint="The head for the search result used as {@link MessageFormat} with the search expression used as argument {0}."/>
            <cpp:widget label="Error text" property="searchtermErrorText" type="richtext" height="100px"
                        hint="A text to show when the user inputs faulty search terms. Describing the syntax is recommended."/>
        </div>
    </div>
</cpp:editDialog>
