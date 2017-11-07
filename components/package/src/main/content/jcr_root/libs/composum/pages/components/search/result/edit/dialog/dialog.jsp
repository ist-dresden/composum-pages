<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<cpp:editDialog var="result" type="com.composum.pages.components.model.search.SearchField"
                title="Edit Search Result">

    <cpp:widget label="Selector" property="selector" type="text" i18n="false" rules="optional"
                hint="Sling selector which renders a found resource as search result. Default &quot;searchItem&quot;."/>

    <cpp:widget label="Template" property="template" type="text" i18n="false" rules="optional"
                hint="Template for printing the search results. Takes precedence over selector."/>

    <cpp:widget label="Search Root" property="searchRoot" type="path" i18n="false" rules="optional"
                hint="Path where the search starts. Default: site-root."/>

    <cpp:widget label="Page size" property="pagesize" type="text" i18n="false" rules="optional"
                hint="Page size for the shown results."/>

    <cpp:widget label="Headline" property="headline" type="textarea" i18n="true" rules="optional"
                hint="The head for the search result (HTML) used as {@link MessageFormat} with the search expression used as argument {0}."/>

    <cpp:widget label="Error text" property="searchtermErrorText" type="textarea" i18n="true" rules="optional"
                hint="A text (HTML) to show when the user inputs faulty search terms. Describing the syntax is recommended."/>

</cpp:editDialog>
