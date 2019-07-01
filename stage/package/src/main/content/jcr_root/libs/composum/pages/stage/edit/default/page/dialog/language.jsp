<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:model var="pagemodel" type="com.composum.pages.commons.model.Page">
    <cpp:editDialogGroup label="Language Split" expanded="true" disabled="${pagemodel.languageSplitLocked}">
        <cpp:widget type="static" i18n="true"
                    value="For the selected languages this page is a language root which is separating the content of the selected languages. In this case the siblings of this page should be the language roots for the other languages."/>
        <cpp:widget type="static" i18n="true" level="warning"
                    value="Caution - The first language of the selected languges defines the default language of this split. This default languge should NOT be chnaged after content in the repository segment has been edited."/>
        <cpp:widget label="Languages" property="pageLanguages" type="multicheck" options="${pagemodel.languages}"
                    hint="the languages of the content tree of this language root"/>
    </cpp:editDialogGroup>
</cpp:model>
