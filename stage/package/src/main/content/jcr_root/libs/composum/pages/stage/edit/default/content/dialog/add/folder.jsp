<%@page session="false" pageEncoding="utf-8" %>
<%--
  a simple dialog to create a new folder in a generic context (as child of any selected resource)

  - is using the SlingPostServlet to create a new ('*') resource ('@{model.path}/*')

  JS...
    /**
     * the dialog to add a new folder as child of the current selection
     */
    dialogs.NewFolderDialog = dialogs.EditDialog.extend({

        initView: function () {
            dialogs.EditDialog.prototype.initView.apply(this);
            this.$primaryType = this.$('.widget-name_jcr_primaryType');
            this.ordered = core.getWidget(this.el, '.widget-name_ordered', core.components.CheckboxWidget);
        },

        doSubmit: function () {
            this.$primaryType.attr('value', this.ordered.getValue() ? 'sling:OrderedFolder' : 'sling:Folder');
            dialogs.EditDialog.prototype.doSubmit.apply(this);
        }
    });
--%>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog var="model" type="com.composum.pages.commons.model.GenericModel" languageContext="false"
                title="Insert a new Folder" selector="generic" resourcePath="*"
                submitLabel="Create" submit="@{model.path}/*" successEvent="content:inserted">
    <%-- the jcr:primaryType depends on the 'ordered' checkbox value and is set before submit by JS --%>
    <cpp:widget name="jcr:primaryType" type="hidden"/>
    <div class="row">
        <div class="col col-xs-8">
            <cpp:widget label="Name" name=":nameHint" placeholder="the repository name" type="textfield"
                        required="true" pattern="^[A-Za-z_][- \\w]*$"/>
        </div>
        <div class="col col-xs-4"><%-- this checkbox value is not submitted because the name starts with '#' --%>
            <cpp:widget label="arrange manually" name="#ordered" type="checkbox" hint="manual sorted folder ?"/>
        </div>
    </div>
    <cpp:widget label="Title" name="jcr:title" placeholder="the more readable title" type="textfield" i18n="false"/>
    <cpp:widget label="Description" name="jcr:description" type="richtext" i18n="false"/>
</cpp:editDialog>
