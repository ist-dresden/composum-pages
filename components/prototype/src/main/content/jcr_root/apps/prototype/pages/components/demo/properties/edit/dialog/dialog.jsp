<%@page session="false" pageEncoding="utf-8" %><%--
--%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %><%--
--%><%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><cpp:defineFrameObjects/>
<cpp:editDialog var="props" type="com.composum.pages.commons.model.Element"
                title="Properties Demo">
    <cpp:editDialogTab tabId="singlevalue" label="Single Value">
        <div class="row">
            <div class="col-lg-6 col-md-6 col-sm-6 col-xs-12">
                <cpp:widget type="text" label="Text Field" property="text" i18n="true"
                            rules="blank" pattern="^[a-zA-Z_.-]+@[a-zA-Z_.-]+$"
                            hint="an email like text<br/>(pattern: '^[a-zA-Z_.-]+@[a-zA-Z_.-]+$')"/>
            </div>
            <div class="col-lg-6 col-md-6 col-sm-6 col-xs-12">
                <cpp:widget type="slider" label="Slider" property="slider" i18n="true" options="1.5;5;0.5;2"
                            hint="a slider can not have an empty<br/>or undefined value (options: '1.5;5;0.5;2')"/>
            </div>
        </div>
        <div class="row">
            <div class="col-lg-6 col-md-6 col-sm-6 col-xs-12">
                <cpp:widget type="select" label="Select" property="select" i18n="true"
                            options="opt1:Option #1,opt2:Option #2,:nothing"/>
                <cpp:widget type="radio" label="Radio" property="radio" i18n="true"
                            options="opt1:Option #1,opt2:Option #2,:nothing"/>
                <cpp:widget type="radiolist" label="Radio List" property="radiolist" i18n="true"
                            options="opt1:Option #1,opt2:Option #2,:nothing"/>
            </div>
            <div class="col-lg-6 col-md-6 col-sm-6 col-xs-12">
                <cpp:widget type="checkbox" label="Check Box" property="checkbox" i18n="true"/>
                <cpp:widget type="dimension" label="Dimension" property="dimension" i18n="true"
                            hint="width & height"/>
                <cpp:widget type="position" label="Position" property="position"
                            hint="coordinates: x, y"/>
                <cpp:widget type="path" label="Path" property="apath" i18n="true"/>
            </div>
        </div>
    </cpp:editDialogTab>
    <cpp:editDialogTab tabId="multivalue" label="Multi Value">
        <cpp:widget type="hidden" name="languages/sling:resourceType" value="composum/pages/stage/edit/site/languages"/>
        <cpp:multiwidget label="Languages" property="languages"
                         modelClass="com.composum.pages.commons.model.properties.Languages"
                         var="language" cssAdd="multiwidget-table">
            <div class="row">
                <cpp:widget type="hidden" name="sling:resourceType" value="composum/pages/stage/edit/site/languages/language"/>
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
                    <cpp:widget label="Dir" name="direction" value="${language.direction}" type="select"
                                options=",ltr,rtl"/>
                </div>
            </div>
        </cpp:multiwidget>
        <div class="row">
            <div class="col-lg-5 col-md-5 col-sm-5 col-xs-5">
                <cpp:widget label="Multi Test 1" property="multitest1" multi="true" type="select" i18n="true"
                            options=",key:label,key-2:--2--,c:=c=,d,e"/>
            </div>
            <div class="col-lg-7 col-md-7 col-sm-7 col-xs-7">
                <cpp:widget label="Multi Test 2" property="multitest2" multi="true" type="slider"
                            hint="a slider can not have an empty or undefined value"/>
            </div>
        </div>
        <cpp:multiwidget label="Multi Table" property="path/to/child/multitest3" var="multi" i18n="true"
                         cssAdd="multiwidget-table">
            <div class="row">
                <div class="col-lg-3 col-md-3 col-sm-3 col-xs-3">
                    <cpp:widget label="Label" name="label" value="${multi.label}" type="text"
                                pattern="[a-z]+" pattern-hint="ein paar Buchstaben" rules="mandatory,blank"/>
                </div>
                <div class="col-lg-2 col-md-2 col-sm-2 col-xs-2">
                    <cpp:widget label="active" property="active" type="checkbox"/>
                </div>
                <div class="col-lg-2 col-md-2 col-sm-2 col-xs-2">
                    <cpp:widget label="Type" property="type" type="select" options="foo,bar"/>
                </div>
                <div class="col-lg-5 col-md-5 col-sm-5 col-xs-5">
                    <cpp:widget label="Weight" property="weight" type="slider"
                                hint="a slider can not have an empty or undefined value"/>
                </div>
            </div>
        </cpp:multiwidget>
    </cpp:editDialogTab>
    <cpp:editDialogTab tabId="richtext" label="Rich Text">
        <cpp:widget type="richtext" label="Rich Text" property="richtext" i18n="true"/>
    </cpp:editDialogTab>
    <cpp:editDialogTab tabId="sourcecode" label="Text / Source">
        <cpp:widget type="codearea" label="Code Area" property="sourcecode" i18n="true" language="groovy"
                    hint="a Groovy script"/>
        <cpp:widget type="textarea" label="Text Area" property="textarea" i18n="true"/>
    </cpp:editDialogTab>
    <cpp:editDialogTab tabId="image" label="Image">
        <cpp:widget type="image" label="Image" property="image" i18n="true"/>
    </cpp:editDialogTab>
</cpp:editDialog>
