<%@page session="false" pageEncoding="utf-8" %><%--
--%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %><%--
--%><%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><cpp:defineFrameObjects/>
<cpp:editToolbar var="element" type="com.composum.pages.commons.model.Element"
                 cssBase="composum-pages-stage-edit-toolbar">
    <cpp:editAction icon="plus" label="Insert Element" title="Insert a new element to decorate"
                    action="window.composum.pages.actions.container.insert"/>
    <cpp:editAction icon="edit" label="Edit" title="Edit the decorator settings"
                    action="window.composum.pages.actions.element.edit"/>
    <cpp:editAction icon="trash" label="Delete" title="Delete the decorator and the content within"
                    action="window.composum.pages.actions.element.delete"/>
</cpp:editToolbar>
