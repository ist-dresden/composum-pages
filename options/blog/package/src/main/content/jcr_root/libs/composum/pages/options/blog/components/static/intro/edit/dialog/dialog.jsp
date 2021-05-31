<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/><%--
  we are editing the page content itself (resourcePath is set to page content)
  therefore dialog selector 'change' is used to disable element deletion... --%>
<cpp:editDialog title="Edit Intro" resourcePath="${currentPage.content.path}" selector="change"
                successEvent="page:reload">
    <cpp:editDialogTab tabId="blog" label="Blog Intro">
        <cpp:widget label="Title" property="jcr:title" type="textfield" i18n="true" required="true"
                    hint="the page title / headline"/>
        <cpp:widget label="Subtitle" property="subtitle" type="textfield" i18n="true"
                    hint="the optional subtitle / slogan"/>
        <cpp:widget label="Intro Text" property="jcr:description" type="richtext" height="300" i18n="true"
                    hint="the Blog / Article intro text shown on top of the blog page"/>
        <div class="row">
            <div class="col col-xs-7">
                <cpp:widget label="Author" property="meta/author" type="textfield" required="true"/>
            </div>
            <div class="col col-xs-5">
                <cpp:widget label="Date" property="meta/date" type="datefield" required="true"/>
            </div>
        </div>
    </cpp:editDialogTab>
    <cpp:editDialogTab tabId="nav" label="Navigation / SEO">
        <div class="row" style="align-items: flex-start;">
            <div class="col col-xs-9">
                <cpp:widget label="Navigation Title" property="navigation/title" type="textfield" i18n="true"/>
                <cpp:widget label="Description / Keywords" property="seo/description" type="textarea" height="120"
                            i18n="true" hint="a short description for search engines"/>
            </div>
            <div class="col col-xs-3">
                <cpp:widget label="Category" property="category" type="textfield" multi="true"/>
                <cpp:widget type="static" i18n="true" level="remark"
                            value="a set of short keywords in the site context; used for searching and filtering and embedded as 'keywords' in the pages meta data"/>
            </div>
        </div>
    </cpp:editDialogTab>
</cpp:editDialog>
