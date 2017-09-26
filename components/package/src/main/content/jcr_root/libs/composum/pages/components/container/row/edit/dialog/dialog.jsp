<%@page session="false" pageEncoding="utf-8" %><%--
--%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %><%--
--%><%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><cpp:defineFrameObjects/>
<cpp:editDialog var="row" type="com.composum.pages.components.model.container.Row"
                title="@{dialog.selector=='create'?'Create a Row':'Row Properties'}">
    <cpp:widget label="columns" property="columns" type="select" options="-12-:100%,-6--6-:50% / 50%,-4--8-:33% / 66%,-8--4-:66% / 33%,-4--4--4-:33% / 33% / 33%,-3--6--3-:25% / 50% / 25%"/>
</cpp:editDialog>
