<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<%--
  - the dialog is using an MicrositePage model instance to access the page properties
  - the selector 'generic' selects the simplified dialog base of edit dialogs (for custom dialogs)
  - a microsite page has no locale variations, the dialog should not display the language context
  - the 'Upload' submit button is triggering a POST request to the pages content URL with the 'upload' selector
  - after a successful upload the result messages are displayed in a simple dialog and a page
    refresh in the Pages edit frame is triggered by a 'component:changed' DOM event
--%>
<cpp:editDialog var="microsite" type="com.composum.pages.options.microsite.model.MicrositePage"
                title="Upload Microsite Content" selector="generic" languageContext="false"
                submitLabel="Upload" submit="@{microsite.contentUploadUrl}"
                successEvent="messages;component:changed">
    <%-- the dialog has only one input field - the file upload input field --%>
    <cpp:widget label="ZIP file" name="archive" type="fileupload"/>
</cpp:editDialog>
