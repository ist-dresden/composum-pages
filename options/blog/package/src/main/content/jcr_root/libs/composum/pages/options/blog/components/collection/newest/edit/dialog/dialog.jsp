<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/><%--
  we are editing the page content itself (resourcePath is set to page content)
  therefore dialog selector 'change' is used to disable element deletion... --%>
<cpp:editDialog title="Newest Blog Article Filter">
    <div class="row">
        <div class="col col-xs-7">
            <cpp:widget label="Author" property="meta/author" type="textfield"/>
        </div>
        <div class="col col-xs-5">
            <cpp:widget label="Date" property="meta/date" type="datefield"/>
        </div>
    </div>
</cpp:editDialog>
