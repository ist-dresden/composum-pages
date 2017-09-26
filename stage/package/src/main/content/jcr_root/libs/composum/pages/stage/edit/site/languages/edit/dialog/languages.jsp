<%@page session="false" pageEncoding="utf-8" %><%--
--%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %><%--
--%><%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><cpp:defineFrameObjects/>
<cpp:multiwidget label="Languages" property="languages"
                 modelClass="com.composum.pages.commons.model.properties.Languages"
                 var="language" cssAdd="multiwidget-table">
    <div class="row">
        <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
            <cpp:widget label="Name" name=":name" value="${language.name}" type="text"/>
        </div>
        <div class="col-lg-2 col-md-2 col-sm-2 col-xs-2">
            <cpp:widget label="Key" property="key" type="text"/>
        </div>
        <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
            <cpp:widget label="Label" property="label" type="text"/>
        </div>
        <div class="col-lg-2 col-md-2 col-sm-2 col-xs-2">
            <cpp:widget label="Dir" name="direction" value="${language.direction}" type="select" options=",ltr,rtl"/>
        </div>
    </div>
</cpp:multiwidget>
