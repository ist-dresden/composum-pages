<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog title="@{dialog.selector=='create'?'Create a Teaser':'Edit Teaser'}">
    <cpp:editDialogTab tabId="teaser" label="Properties">
        <div class="row">
            <div class="col col-xs-8">
                <cpp:widget label="Teaser Title" property="title" type="textfield" i18n="true"/>
            </div>
            <div class="col col-xs-4">
                <cpp:widget label="Variation" property="variation" type="select" hint="render type"
                            options="default,bgimage,bgvideo,symbol" default="default"/>
            </div>
        </div>
        <cpp:widget label="Subtitle" property="subtitle" type="textfield" i18n="true"/>
        <cpp:widget label="Text" property="text" type="richtext" i18n="true" required="true"
                    height="120px"/>
        <cpp:include resourceType="composum/pages/components/element/link" subtype="edit/dialog"
                     replaceSelectors="embedded"/>
    </cpp:editDialogTab>
    <cpp:editDialogTab tabId="image" label="Image">
        <cpp:include path="image" resourceType="composum/pages/components/element/image" subtype="edit/dialog"
                     replaceSelectors="embedded"/>
    </cpp:editDialogTab>
    <cpp:editDialogTab tabId="video" label="Video">
        <cpp:include path="video" resourceType="composum/pages/components/element/video" subtype="edit/dialog"
                     replaceSelectors="embedded"/>
    </cpp:editDialogTab>
    <cpp:editDialogTab tabId="icon" label="Icon">
        <div class="row">
            <div class="col col-xs-7">
                <cpp:widget type="static"
                            value="Alternative to a teaser image or video a symbol can be used. The chosen symbol is not used if an image or video is referenced."/>
            </div>
            <div class="col col-xs-5">
                <cpp:widget label="Symbol" property="icon" type="iconcombobox"
                            hint="<a href='https://fontawesome.com/v4.7.0/icons/' target='_blank'>'FontAwesome'</a> icon key"
                            options="at,asterisk,bookmark-o:bookmark,check,exclamation,eye,info-circle:info,lightbulb-o:lightbulb,question-circle-o:qestion,search,warning,wrench"
                            typeahead="/bin/cpm/core/system.typeahead.json/libs/fonts/awesome/4.7.0/font-awesome-keys.txt"/>
            </div>
        </div>
    </cpp:editDialogTab>
</cpp:editDialog>
