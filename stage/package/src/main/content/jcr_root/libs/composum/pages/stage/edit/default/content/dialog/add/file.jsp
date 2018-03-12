<%@page session="false" pageEncoding="utf-8" %>
<%--
  a dialog to upload a new file in a generic context (as child of any selected resource)

  - is using the SlingPostServlet to create a new ('*') resource of type 'nt:file'

  JS...
    /**
     * the dialog to upload a file as child of the current selection
     */
    dialogs.NewFileDialog = dialogs.EditDialog.extend({

        initView: function () {
            dialogs.EditDialog.prototype.initView.apply(this);
            this.file = core.getWidget(this.el, '.widget-name_STAR', core.components.FileUploadWidget);
            this.name = core.getWidget(this.el, '.widget-name_name', core.components.TextFieldWidget);
        },

        doSubmit: function () {
            var name = this.name.getValue();
            if (!name) {
                name = this.file.getFileName();
            }
            if (name && (name = core.mangleNameValue(name))) {
                this.file.setName(name);
            }
            dialogs.EditDialog.prototype.doSubmit.apply(this);
        }
    });
--%>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog var="model" type="com.composum.pages.commons.model.GenericModel" languageContext="false"
                title="Insert a new File" selector="generic"
                submitLabel="Upload" submit="@{model.path}">
    <cpp:widget type="hidden" name="*@TypeHint" value="nt:file"/>
    <%-- the name is replaced by the mangeled file or name input field value --%>
    <cpp:widget label="File" name="*" type="fileupload" rules="mandatory"/>
    <%-- the name field is only used by the dialog (not submitted; starts with '#') --%>
    <cpp:widget label="Name" name="#name" placeholder="the repository name" type="textfield"
                rules="blank" pattern="^[A-Za-z_][- \\w]*(\\.\\w+)?$"
                hint="add a name if the file name is not the right choice"/>
</cpp:editDialog>
