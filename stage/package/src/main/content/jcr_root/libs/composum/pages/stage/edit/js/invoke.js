(function (window) {
    window.composum = window.composum || {};
    window.composum.pages = window.composum.pages || {};
    window.composum.pages.elements = window.composum.pages.elements || {};

    (function (elements, core) { // the small hook to use edit functions in a normal content page
        'use strict';

        elements.const = _.extend(elements.const || {}, {
            data: { // the data attribute names of a component
                name: 'pages-edit-name',
                path: 'pages-edit-path',
                type: 'pages-edit-type',
                url: 'pages-edit-url',
                action: 'pages-edit-action'
            },
            class: { // general edit UI CSS classes
                editBody: 'composum-pages-EDIT_body',
                component: 'composum-pages-component',
                container: 'composum-pages-container',
                element: 'composum-pages-element',
                action: 'composum-pages-action'
            },
            event: { // event handling rules and keys (inter frame communication)
                messagePattern: new RegExp('^([^\\{\\[]+)([\\{\\[].*[\\}\\]])$'),
                pageContainerRefs: 'page:containerRefs',
                componentSelect: 'component:select',
                componentSelected: 'component:selected',
                insertComponent: 'component:insert',
                moveComponent: 'component:move',
                openEditDialog: 'dialog:edit',
                alertMessage: 'dialog:alert'
            }
        });

        elements.alertMessage = function (type, title, message, data) {
            // call the action in the 'edit' layer of the UI
            parent.postMessage(elements.const.event.alertMessage
                + JSON.stringify({
                    type: type,
                    title: title,
                    message: message,
                    data: data
                }), '*');
        };

        elements.openEditDialog = function (target, dialog, values) {
            // call the action in the 'edit' layer of the UI
            parent.postMessage(elements.const.event.openEditDialog
                + JSON.stringify({
                    target: target,
                    dialog: dialog,
                    values: values
                }), '*');
        };

        /**
         * the action view to open an edit dialog
         */
        elements.OpenEditDialogAction = Backbone.View.extend({

            initialize: function (options) {
                // collect the component reference data
                this.data = {
                    path: this.$el.data(elements.const.data.path),
                    type: this.$el.data(elements.const.data.type),
                    url: this.$el.data(elements.const.data.url)
                };
                this.$el.click(_.bind(this.onClick, this));
            },

            onClick: function (event) {
                if (event) {
                    event.preventDefault();
                }
                elements.openEditDialog({
                    path: this.data.path,
                    type: this.data.type
                }, this.data.url ? {
                    url: url
                } : undefined);
                return false;
            }
        });

        // register all appropriate 'openEditDialog' actions
        $('.' + elements.const.class.action + '[data-' + elements.const.data.action + '="openEditDialog"]').each(function () {
            core.getView(this, elements.OpenEditDialogAction);
        });

    })(window.composum.pages.elements, window.core);
})(window);
