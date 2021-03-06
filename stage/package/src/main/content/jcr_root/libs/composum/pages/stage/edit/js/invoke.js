/**
 * functions for a content page to invoke edit functions of the Pages edit frame
 * strong dependency to: 'commons.js' (libs: 'backbone.js', 'underscore.js', 'loglevel.js', 'jquery.js')
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
                    custom: 'dialog:custom',
                    alert: 'dialog:alert'
                }
            },
            event: {
                messagePattern: new RegExp('^([^{\\[]+)([{\\[].*[}\\]])$'),
                pages: {
                    ready: 'pages:ready',
                    locale: 'pages:locale',
                    open: 'pages:open'
                },
                scope: {
                    changed: 'scope:changed'
                },
                site: {
                    select: 'site:select',          // do it!...
                    selected: 'site:selected',      // done.
                    created: 'site:created',        // done.
                    changed: 'site:changed',        // done.
                    deleted: 'site:deleted'         // done.
                },
                page: {
                    select: 'page:select',          // do it!...
                    selected: 'page:selected',      // done.
                    inserted: 'page:inserted',      // done.
                    changed: 'page:changed',        // done.
                    deleted: 'page:deleted',        // done.
                    containerRefs: 'page:containerRefs'
                },
                content: {
                    view: 'content:view',              // do it!...
                    select: 'content:select',       // do it!...
                    selected: 'content:selected',   // done.
                    inserted: 'content:inserted',   // done.
                    changed: 'content:changed',     // done.
                    deleted: 'content:deleted',     // done.
                    moved: 'content:moved',         // done.
                    state: 'content:state'          // changed state of the content itself only (no page structure change)
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
                },
                asset: {
                    select: 'asset:select',         // do it!...
                    selected: 'asset:selected'      // done.
                },
                folder: {
                    inserted: 'folder:inserted'     //done.
                },
                path: {
                    select: 'path:select',          // do it!...
                    selected: 'path:selected'       // done.
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

        elements.log = {
            std: log.getLogger("elements:std"),
            dnd: log.getLogger("elements:dnd"),
            ptr: log.getLogger("elements:ptr")
        };

        /**
         * the general $(document) trigger point for the 'element' widgets of the edited page
         * @param key the logging key which identifies the code source
         * @param event the event to trigger
         * @param args the event arguments (array of objects or values)
         * @param argsToLog optional; used for logging instead of the event args if args should not be logged
         */
        elements.trigger = function (key, event, /*array,optional*/ args, /*optional*/ argsToLog) {
            if (elements.log.std.getLevel() <= log.levels.WARN) { // use WARN to cause a call stack
                elements.log.std.warn('trigger@' + key + ' > ' + event
                    + JSON.stringify(argsToLog !== undefined ? argsToLog : (args ? args : [])));
            }
            $(document).trigger(event, args);
        };

        /**
         * show an alert message in the edit frame
         * @param type
         * @param title
         * @param message
         * @param data
         */
        elements.alertMessage = function (type, title, message, data) {
            if (elements.log.std.getLevel() <= log.levels.DEBUG) {
                elements.log.std.debug('elements.postMessage.' + elements.const.trigger.dialog.alert + '('
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
            if (elements.log.std.getLevel() <= log.levels.DEBUG) {
                elements.log.std.debug('elements.postMessage.' + elements.const.trigger.event + '('
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
            if (elements.log.std.getLevel() <= log.levels.DEBUG) {
                elements.log.std.debug('elements.postMessage.' + elements.const.trigger.action + '('
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
         * open a dialog loaded in the edit frames context with a raw dialog parameter set
         * @param url the url to load the dialog (includes necessary selectors, extension, suffix and parameters)
         * @param type the type (class name) of the dialog behaviour implementation
         * @param config the config object to initialize the dialog
         * @param init a function reference (will be evaluated) to initialize the dialog
         * @param trigger the event to trigger as dialogs callback
         */
        elements.openCustomDialog = function (url, type, config, init, trigger) {
            if (elements.log.std.getLevel() <= log.levels.DEBUG) {
                elements.log.std.debug('elements.postMessage.' + elements.const.trigger.dialog.custom + '('
                    + url + ','
                    + type + ')');

            }
            // call the action in the 'edit' layer of the UI
            parent.postMessage(elements.const.trigger.dialog.custom
                + JSON.stringify({
                    url: url,
                    type: type,
                    config: config,
                    init: init,
                    trigger: trigger
                }), '*');
        };

        /**
         * open a dialog loaded in the edit frames context with the generic 'editResource' selector
         * @param target
         * @param dialog
         * @param values the values object transmitted as PUT data object (JSON)
         */
        elements.openGenericDialog = function (target, dialog, values) {
            if (elements.log.std.getLevel() <= log.levels.DEBUG) {
                elements.log.std.debug('elements.postMessage.' + elements.const.trigger.dialog.generic + '('
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
         * open the edit dialog for an element of the page in the edit frames context
         * @param target
         * @param dialog
         * @param values
         */
        elements.openEditDialog = function (target, dialog, values) {
            if (elements.log.std.getLevel() <= log.levels.DEBUG) {
                elements.log.std.debug('elements.postMessage.' + elements.const.trigger.dialog.edit + '('
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
