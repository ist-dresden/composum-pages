(function (window) {
    window.composum = window.composum || {};
    window.composum.pages = window.composum.pages || {};
    window.composum.pages.actions = window.composum.pages.actions || {};

    (function (actions, pages, core) {
        'use strict';

        /**
         * the set of actions usable in the 'action' attribute of a toolbar action tag or somewhere else
         *
         * general parameters:
         * @param event the event object in the UI
         * @param name the name of the content element
         * @param path the path of the content element
         * @param type the resource type of the content element
         */

        actions.const = _.extend(actions.const || {}, {});

        actions.dialog = {

            /**
             * opens an edit dialog (additional custom dialog) selected by the 'selectors' attribute of the event
             * (e.g. an action of a toolbar with an appropriate 'selectors' attribute)
             */
            open: function (event, name, path, type) {
                var $action = $(event.currentTarget);
                var selectors = $action.data('selectors');
                var dialogUrl = pages.dialogs.getEditDialogUrl('load', selectors);
                pages.dialogs.openEditDialog(name, path, type, dialogUrl);
            }
        };

        actions.element = {

            edit: function (event, name, path, type) {
                pages.dialogs.openEditDialog(name, path, type);
            },

            copy: function (event, name, path, type) {
                alert('element.copy... ' + name + ',' + path + ',' + type);
            },

            paste: function (event, name, path, type) {
                alert('element.paste... ' + name + ',' + path + ',' + type);
            },

            delete: function (event, name, path, type) {
                pages.dialogs.openDeleteElementDialog(name, path, type);
            }
        };

        actions.asset = {

            show: function (event, name, path, type) {
                alert('asset.show... ' + name + ',' + path + ',' + type);
            }
        };

        actions.container = {

            edit: function (event, name, path, type) {
                actions.element.edit(event, name, path, type);
            },

            insert: function (event, name, path, type) {
                pages.dialogs.openNewElementDialog(name, path, type);
            }
        };

        actions.page = {

            edit: function (event, name, path, type) {
                actions.element.edit(event, name, path, type);
            },

            insert: function (event, name, path, type) {
                alert('page.insert... ' + name + ',' + path + ',' + type);
            },

            copy: function (event, name, path, type) {
                alert('page.copy... ' + name + ',' + path + ',' + type);
            },

            paste: function (event, name, path, type) {
                alert('page.paste... ' + name + ',' + path + ',' + type);
            },

            rename: function (event, name, path, type) {
                alert('page.rename... ' + name + ',' + path + ',' + type);
            },

            move: function (event, name, path, type) {
                alert('page.move... ' + name + ',' + path + ',' + type);
            },

            delete: function (event, name, path, type) {
                alert('page.delete... ' + name + ',' + path + ',' + type);
            },

            checkout: function (event, name, path, type) {
                alert('page.checkout... ' + name + ',' + path + ',' + type);
            },

            checkin: function (event, name, path, type) {
                alert('page.checkin... ' + name + ',' + path + ',' + type);
            },

            checkpoint: function (event, name, path, type) {
                alert('page.checkpoint... ' + name + ',' + path + ',' + type);
            },

            lock: function (event, name, path, type) {
                alert('page.lock... ' + name + ',' + path + ',' + type);
            }
        };

        actions.site = {

            edit: function (event, name, path, type) {
                actions.element.edit(event, name, path, type);
            },

            insert: function (event, name, path, type) {
                pages.dialogs.openNewContentDialog();
            },

            create: function (event, name, path, type) {
                pages.dialogs.openCreateSiteDialog();
            },

            delete: function (event, name, path, type) {
                pages.dialogs.openDeleteSiteDialog(name, path, type);
            },

            rename: function (event, name, path, type) {
                alert('site.rename... ' + name + ',' + path + ',' + type);
            },

            move: function (event, name, path, type) {
                alert('site.move... ' + name + ',' + path + ',' + type);
            }
        };

        actions.folder = {

            insert: function (event, name, path, type) {
                alert('folder.insert... ' + name + ',' + path + ',' + type);
            },

            rename: function (event, name, path, type) {
                alert('folder.rename... ' + name + ',' + path + ',' + type);
            },

            move: function (event, name, path, type) {
                alert('folder.move... ' + name + ',' + path + ',' + type);
            },

            copy: function (event, name, path, type) {
                alert('folder.copy... ' + name + ',' + path + ',' + type);
            },

            paste: function (event, name, path, type) {
                alert('folder.paste... ' + name + ',' + path + ',' + type);
            },

            delete: function (event, name, path, type) {
                alert('folder.delete... ' + name + ',' + path + ',' + type);
            }
        };

    })(window.composum.pages.actions, window.composum.pages, window.core);
})(window);
