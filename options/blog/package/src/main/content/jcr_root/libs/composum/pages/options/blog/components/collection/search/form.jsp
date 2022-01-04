<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<cpn:bundle basename="composum-pages-option-blog"/>
<cpp:model var="model" type="com.composum.pages.options.blog.model.NewestArticles">
    <cpp:widgetForm method="GET" action="${currentPage.url}" role="search">
        <div class="row">
            <div class="col col-xs-8">
                <cpp:widget type="textfield" label="Text" name="term" value="${model.pattern}"
                            hint="a search text pattern to filter the articles by their content"/>
            </div>
            <div class="col col-xs-4">
                <cpp:widget type="textfield" label="Author" name="author" value="${model.author}"/>
            </div>
        </div>
        <div class="row">
            <div class="col col-xs-5 col-sm-4">
                <cpp:widget type="select" label="Date" name="period" value="${model.period}"
                            options="lastMonth:last month,lastYear:last year,about,before,after"
                            default="about"/>
            </div>
            <div class="col col-xs-5 col-sm-4">
                <cpp:widget type="datefield" label="" name="date" value="${model.date}"
                            hint="a period to filter the articles by date"/>
            </div>
            <div class="col col-xs-2">
                <button type="submit" class="btn btn-default btn-col fa fa-search"
                        title="${cpn:i18n(slingRequest,'Filter Articles')}..."></button>
            </div>
        </div>
    </cpp:widgetForm>
</cpp:model>
