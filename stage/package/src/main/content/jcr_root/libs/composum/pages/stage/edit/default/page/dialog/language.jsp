<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineFrameObjects/>
<cpp:model var="pagemodel" type="com.composum.pages.commons.model.Page">
    <cpp:editDialogGroup label="Language Split" expanded="true" disabled="${pagemodel.languageSplitLocked}">
        <c:choose>
            <c:when test="${pagemodel.languageSplitLocked}">
                <cpp:widget type="static" i18n="true"
                            value="This page is part of a language split which is separating the content of the selected languages."/>
                <cpp:model var="languageRoot" type="com.composum.pages.commons.model.LanguageRoot">
                    <div class="form-group">${cpn:i18n(slingRequest,'the language root is:')}
                        <cpn:link href="/bin/pages.html${cpn:path(languageRoot.path)}">${cpn:text(languageRoot.path)}</cpn:link>
                    </div>
                </cpp:model>
            </c:when>
            <c:otherwise>
                <cpp:widget type="static" i18n="true"
                            value="For the selected languages this page is or can be a language root which is separating the content of the selected languages. In this case the siblings of this page should be the language roots for the other languages."/>
                <cpp:widget type="static" i18n="true" level="warning"
                            value="Caution - The first language of the selected languages defines the default language of this split. This default language should NOT be changed after content in this repository segment has been edited."/>
            </c:otherwise>
        </c:choose>
        <cpp:widget label="Languages" name="pageLanguages" value="${pagemodel.languageKeys}" type="multicheck"
                    options="${pagemodel.languages}" hint="the languages of the content tree of this language root"/>
    </cpp:editDialogGroup>
</cpp:model>
