<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:model var="site" type="com.composum.pages.commons.model.Site">
    <cpp:editDialog var="sites" type="com.composum.pages.commons.model.Sites" selector="generic" languageContext="false"
                    title="Clone Site" submitLabel="Clone" submit="/bin/cpm/pages/edit.cloneSite.json@{site.path}"
                    alert-danger="Do you really want to clone (copy) the entire site?">
        <div class="row">
            <cpn:div test="${sites.tenantSupport}" class="col col-xs-5">
                <cpp:widget label="Tenant" name="tenant" type="select" required="true"
                            options="${sites.tenantOptions}"/>
            </cpn:div>
            <div class="col col-xs-${sites.tenantSupport?'7':'12'}">
                <cpp:widget label="Name" name="name" placeholder="the repository name" type="textfield"
                            required="true" pattern="/^[\\p{L}_][\\p{N}\\p{L} _-]*([\\p{L}_][\\p{N}\\p{L} _-]*/)?$/u"
                            pattern-hint="a letter followed by letters, digits, blanks; optional one path segment"/>
            </div>
        </div>
        <cpp:widget label="Title" name="jcr:title" placeholder="the more readable title" type="textfield" value="${site.title}"/>
        <cpp:widget label="Description" name="jcr:description" type="richtext" value="${site.description}"/>
    </cpp:editDialog>
</cpp:model>
