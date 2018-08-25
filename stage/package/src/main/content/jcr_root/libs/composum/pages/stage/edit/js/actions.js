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
                pages.dialogs.openEditDialog(name, path, type, undefined/*context*/, dialogUrl);
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

            insertPage: function (event, name, path, type) {
                pages.dialogs.openNewPageDialog(name, path, type);
            },

            insertFolder: function (event, name, path, type) {
                pages.dialogs.openNewFolderDialog(name, path, type);
            },

            insertFile: function (event, name, path, type) {
                pages.dialogs.openNewFileDialog(name, path, type);
            },

            copy: function (event, name, path, type) {
                pages.clipboardCopyContent(path);
            },

            paste: function (event, name, path, type) {
                pages.clipboardPasteContent(path);
            },

            rename: function (event, name, path, type) {
                pages.dialogs.openRenameContentDialog(name, path, type)
            },

            move: function (event, name, path, type) {
                pages.dialogs.openMoveContentDialog(name, path, type)
            },

            delete: function (event, name, path, type) {
                pages.dialogs.openDeleteContentDialog('page', name, path, type);
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

            insertPage: function (event, name, path, type) {
                pages.dialogs.openNewPageDialog(name, path, type);
            },

            insertFolder: function (event, name, path, type) {
                pages.dialogs.openNewFolderDialog(name, path, type);
            },

            insertFile: function (event, name, path, type) {
                pages.dialogs.openNewFileDialog(name, path, type);
            },

            create: function (event, name, path, type) {
                pages.dialogs.openCreateSiteDialog(name, path, type);
            },

            rename: function (event, name, path, type) {
                pages.dialogs.openRenameContentDialog(name, path, type)
            },

            move: function (event, name, path, type) {
                pages.dialogs.openMoveContentDialog(name, path, type)
            },

            copy: function (event, name, path, type) {
                pages.clipboardCopyContent(path);
            },

            paste: function (event, name, path, type) {
                pages.clipboardPasteContent(path);
            },

            delete: function (event, name, path, type) {
                pages.dialogs.openDeleteSiteDialog(name, path, type);
            }
        };

        actions.folder = {

            edit: function (event, name, path, type) {
                var c = pages.dialogs.const.edit.url;
                pages.dialogs.openEditDialog(name, path, type, undefined/*context*/, c.path + c._edit._folder);
            },

            insertPage: function (event, name, path, type) {
                pages.dialogs.openNewPageDialog(name, path, type);
            },

            insertFolder: function (event, name, path, type) {
                pages.dialogs.openNewFolderDialog(name, path, type);
            },

            insertFile: function (event, name, path, type) {
                pages.dialogs.openNewFileDialog(name, path, type);
            },

            rename: function (event, name, path, type) {
                pages.dialogs.openRenameContentDialog(name, path, type)
            },

            move: function (event, name, path, type) {
                pages.dialogs.openMoveContentDialog(name, path, type)
            },

            copy: function (event, name, path, type) {
                pages.clipboardCopyContent(path);
            },

            paste: function (event, name, path, type) {
                pages.clipboardPasteContent(path);
            },

            delete: function (event, name, path, type) {
                pages.dialogs.openDeleteContentDialog('folder', name, path, type);
            }
        };

        actions.file = {

            rename: function (event, name, path, type) {
                pages.dialogs.openRenameContentDialog(name, path, type)
            },

            move: function (event, name, path, type) {
                pages.dialogs.openMoveContentDialog(name, path, type)
            },

            copy: function (event, name, path, type) {
                pages.clipboardCopyContent(path);
            },

            paste: function (event, name, path, type) {
                pages.clipboardPasteContent(path);
            },

            delete: function (event, name, path, type) {
                pages.dialogs.openDeleteContentDialog('file', name, path, type);
            }
        };

        actions.dnd = {

            doDrop: function (event, target, object) {
                if (target && target.container.data.path && object) {
                    switch (object.type) {
                        case 'component': // insert a new component
                            actions.dnd.doDropInsert(event, target, object);
                            break;
                        case 'element': // copy or move an element
                            switch (target.operation) {
                                case 'copy': // copy the element
                                    actions.dnd.doDropCopy(event, target, object);
                                    break;
                                default: // move the element
                                    actions.dnd.doDropMove(event, target, object);
                                    break;
                            }
                            break;
                    }
                }
            },

            doDropInsert: function (event, target, object) {
                if (target && target.container.data.path && object && object.type === 'component') {
                    pages.dnd.insertNewElement(target, object);
                }
            },

            doDropCopy: function (event, target, object) {
                if (target && target.container.data.path && object && object.type === 'element') {
                }
            },

            doDropMove: function (event, target, object) {
                if (target && target.container.data.path && object && object.type === 'element') {
                }
            }
        };

    })(window.composum.pages.actions, window.composum.pages, window.core);
})(window);
