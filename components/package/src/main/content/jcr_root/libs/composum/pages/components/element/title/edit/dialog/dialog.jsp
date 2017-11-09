<%@page session="false" pageEncoding="utf-8" %><%--
--%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><cpp:defineFrameObjects/>
<cpp:editDialog var="text" type="com.composum.pages.components.model.title.Title"
                title="@{dialog.selector=='create'?'Create a Title':'Edit Title'}">
    <cpp:widget label="Title" property="title" type="text" i18n="true"/>
    <cpp:widget label="Subtitle" property="subtitle" type="text" i18n="true"/>
    <sling:include path="image" replaceSelectors="imageOnly"
                   resourceType="composum/pages/components/element/image/edit/dialog"/>
</cpp:editDialog>
