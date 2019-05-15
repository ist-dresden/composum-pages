/**
 * functions for a content page to invoke edit functions of the Pages edit frame
 */
(function (window) {
    window.composum = window.composum || {};
    window.composum.pages = window.composum.pages || {};
    window.composum.pages.elements = window.composum.pages.elements || {};

    /**
     * the JS hook for content pages to trigger Pages stage actions and synchronize the page view with the edit state
     */
    (function (elements, pages, core) { // the small hook to use edit functions in a normal content page
        'use strict';

        pages.current = {
            element: undefined,
            dnd: {}
        };

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
                dropzone: 'composum-pages-edit_drop-zone',
                action: 'composum-pages-action'
            },
            trigger: {
                event: 'trigger:event',
                action: 'trigger:action',
                dialog: {
                    edit: 'dialog:edit',
                    generic: 'dialog:generic',
                    alert: 'dialog:alert'
                }
            },
            event: { // event handling rules and keys (inter frame communication)
                messagePattern: new RegExp('^([^{\\[]+)([{\\[].*[}\\]])$'),
                site: {
                    changed: 'site:changed'         // done.
                },
                page: {
                    changed: 'page:changed',        // done.
                    containerRefs: 'page:containerRefs'
                },
                element: {
                    select: 'element:select',       // do it!...
                    selected: 'element:selected',   // done.
                    inserted: 'element:inserted',   // done.
                    changed: 'element:changed',     // done.
                    deleted: 'element:deleted',     // done.
                    moved: 'element:moved'          // done.
                },
                dnd: {
                    object: 'dnd:object',           // prepare dragging
                    drop: 'dnd:drop',               // do it!...
                    finished: 'dnd:finished'        // done, reset state.
                }
            },
            url: {
                edit: '/bin/cpm/pages/edit',
                _info: '.resourceInfo.json'
            }
        });

        elements.log = log.getLogger("elements");

        /**
         * show an alert message in the edit frame
         * @param type
         * @param title
         * @param message
         * @param data
         */
        elements.alertMessage = function (type, title, message, data) {
            if (elements.log.getLevel() <= log.levels.DEBUG) {
                elements.log.debug('elements.postMessage.' + elements.const.trigger.dialog.alert + '('
                    + type + ','
                    + title + ','
                    + message + ','
                    + data + ')');

            }
            // call the action in the 'edit' layer of the UI
            parent.postMessage(elements.const.trigger.dialog.alert
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
            if (elements.log.getLevel() <= log.levels.DEBUG) {
                elements.log.debug('elements.postMessage.' + elements.const.trigger.event + '('
                    + event + ','
                    + data + ')');

            }
            // trigger the event in the 'edit' layer of the UI
            parent.postMessage(elements.const.trigger.event
                + JSON.stringify({
                    event: event,
                    data: data
                }), '*');
        };

        /**
         * perform an action in the edit frame
         * @param event
         * @param data
         */
        elements.triggerAction = function (action, reference) {
            if (elements.log.getLevel() <= log.levels.DEBUG) {
                elements.log.debug('elements.postMessage.' + elements.const.trigger.action + '('
                    + action + ','
                    + reference + ')');

            }
            // trigger the action in the 'edit' layer of the UI
            parent.postMessage(elements.const.trigger.action
                + JSON.stringify({
                    action: action,
                    reference: reference
                }), '*');
        };

        /**
         * open a dialog loaded via PUT with the generic 'editResource' selector
         * @param target
         * @param dialog
         * @param values the values object transmitted as PUT data object (JSON)
         */
        elements.openGenericDialog = function (target, dialog, values) {
            if (elements.log.getLevel() <= log.levels.DEBUG) {
                elements.log.debug('elements.postMessage.' + elements.const.trigger.dialog.generic + '('
                    + target + ','
                    + dialog + ','
                    + values + ')');

            }
            // call the action in the 'edit' layer of the UI
            parent.postMessage(elements.const.trigger.dialog.generic
                + JSON.stringify({
                    target: target,
                    dialog: dialog,
                    values: values
                }), '*');
        };

        /**
         * open the edit dialog for an element of the page
         * @param target
         * @param dialog
         * @param values
         */
        elements.openEditDialog = function (target, dialog, values) {
            if (elements.log.getLevel() <= log.levels.DEBUG) {
                elements.log.debug('elements.postMessage.' + elements.const.trigger.dialog.edit + '('
                    + target + ','
                    + dialog + ','
                    + values + ')');

            }
            // call the action in the 'edit' layer of the UI
            parent.postMessage(elements.const.trigger.dialog.edit
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

    })(window.composum.pages.elements, window.composum.pages, window.core);
})(window);
