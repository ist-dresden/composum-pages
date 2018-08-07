(function (window) {
    window.composum = window.composum || {};
    window.composum.pages = window.composum.pages || {};
    window.composum.pages.elements = window.composum.pages.elements || {};

    /**
     * the JS hook for content pages to trigger Pages stage actions and synchronize the page view with the edit state
     */
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
                messagePattern: new RegExp('^([^{\\[]+)([{\\[].*[}\\]])$'),
                trigger: 'event:trigger',
                dialog: {
                    edit: 'dialog:edit',
                    alert: 'dialog:alert'
                },
                page: {
                    containerRefs: 'page:containerRefs'
                },
                element: {
                    select: 'element:select',       // do it!...
                    selected: 'element:selected',   // done.
                    insert: 'element:insert',       // do it!...
                    inserted: 'element:inserted',   // done.
                    changed: 'element:changed',     // done.
                    deleted: 'element:deleted',     // done.
                    move: 'element:move',           // do it!...
                    moved: 'element:moved'          // done.
                }
            }
        });

        /**
         * show an alert message in the edit frame
         * @param type
         * @param title
         * @param message
         * @param data
         */
        elements.alertMessage = function (type, title, message, data) {
            // call the action in the 'edit' layer of the UI
            parent.postMessage(elements.const.event.dialog.alert
                + JSON.stringify({
                    type: type,
                    title: title,
                    message: message,
                    data: data
                }), '*');
        };

        /**
         * send an event to the edit frame
         * @param event
         * @param data
         */
        elements.triggerEvent = function (event, data) {
            // trigger the event in the 'edit' layer of the UI
            parent.postMessage(elements.const.event.trigger
                + JSON.stringify({
                    event: event,
                    data: data
                }), '*');
        };

        /**
         * open the edit dialog for an element of the page
         * @param target
         * @param dialog
         * @param values
         */
        elements.openEditDialog = function (target, dialog, values) {
            // call the action in the 'edit' layer of the UI
            parent.postMessage(elements.const.event.dialog.edit
                + JSON.stringify({
                    target: target,
                    dialog: dialog,
                    values: values
                }), '*');
        };

        /**
         * the action view to open an edit dialog (for buttons, links, ...)
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

        // register all appropriate 'openEditDialog' actions (data-pages-edit-action="openEditDialog")
        $('.' + elements.const.class.action + '[data-' + elements.const.data.action + '="openEditDialog"]').each(function () {
            core.getView(this, elements.OpenEditDialogAction);
        });

    })(window.composum.pages.elements, window.core);
})(window);
