<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/><%--
  we are editing the page content itself (resourcePath is set to page content)
  therefore dialog selector 'change' is used to disable element deletion... --%>
<cpp:editDialog var="model" type="com.composum.pages.options.blog.model.BlogIntro"
                title="Edit Intro" resourcePath="${currentPage.content.path}" selector="change"
                successEvent="page:reload">
    <cpp:editDialogTab tabId="blog" label="Blog Intro">
        <cpp:include replaceSelectors="${model.blogRoot?'header':'intro'}"/>
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
