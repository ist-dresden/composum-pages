<%@page session="false" pageEncoding="utf-8" %><%--
--%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><cpp:defineFrameObjects/>
<cpp:editDialog var="text" type="com.composum.pages.components.model.textimage.TextImage"
                title="Edit Text and Image">
    <cpp:editDialogTab tabId="text" label="Text">
        <sling:call script="embedded.jsp"/>
    </cpp:editDialogTab>
    <cpp:editDialogTab tabId="image" label="Image">
        <sling:include path="image" replaceSelectors="optional"
                       resourceType="composum/pages/components/element/image/edit/dialog"/>
    </cpp:editDialogTab>
</cpp:editDialog>
