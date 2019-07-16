<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:multiwidget label="Languages" property="languages"
                 modelClass="com.composum.pages.commons.model.properties.Languages"
                 var="language" cssAdd="multiwidget-table">
    <div class="row">
        <div class="col col-xs-4"><cpp:widget label="Name" name=":name" value="${language.name}"
                                              type="textfield"/></div>
        <div class="col col-xs-2"><cpp:widget label="Key" name="key" value="${language.key}" type="textfield"/></div>
        <div class="col col-xs-4"><cpp:widget label="Label" name="label" value="${language.label}"
                                              type="textfield"/></div>
        <div class="col col-xs-2"><cpp:widget label="Dir" name="direction" value="${language.direction}" type="select"
                                              options=",ltr,rtl"/></div>
    </div>
</cpp:multiwidget>
