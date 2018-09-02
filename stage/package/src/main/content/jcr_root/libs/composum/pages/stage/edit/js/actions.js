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
            },

            delete: function (event, name, path, type) {
                actions.element.delete(event, name, path, type);
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

        //
        // DnD operations
        //

        actions.dnd = {

            /**
             * handler to drop an object into a new target or at a new position;
             * if the object is a component (type: 'component') a new element is inserted at the designated position
             * @param target a target description: { container: {reference: reference}(, before: {reference: reference})}
             * @param object the object to drop (element or component): { type: 'component'|'element', reference: reference}
             */
            doDrop: function (target, object) {
                if (target && target.container.reference.path && object) {
                    switch (object.type) {
                        case 'component': // insert a new component
                            actions.dnd.doDropInsert(target, object);
                            break;
                        case 'element': // copy or move an element
                            switch (target.operation || object.operation) {
                                case 'copy': // copy the element
                                    actions.dnd.doDropCopy(target, object);
                                    break;
                                default: // move the element
                                    actions.dnd.doDropMove(target, object);
                                    break;
                            }
                            break;
                    }
                }
            },

            doDropInsert: function (target, object, context) {
                if (target && target.container.reference.path && object && object.type === 'component') {
                    if (!context) {
                        context = {
                            parent: target.container.reference,
                            before: target.before.reference
                        };
                    }
                    if (!context.parent.isComplete()) {
                        // get resource type and/or primary type of potentially synthetic parent and retry...
                        context.parent.complete(function () {
                            actions.dnd.doDropInsert(target, object, context);
                        });
                    } else {
                        var d = pages.dialogs.const.edit.url;
                        pages.dialogs.openCreateDialog('*', context.parent.path, object.reference.type,
                            context, undefined, undefined,
                            // if no create dialog exists (not found) create a new instance directly
                            _.bind(function (name, path, type) {
                                core.ajaxPost(d.base + d._insert, {
                                    _charset_: 'UTF-8',
                                    resourceType: type,
                                    targetPath: path,
                                    targetType: target.container.reference.type
                                }, {}, _.bind(function () {
                                    pages.log.debug('pages.trigger.' + pages.const.event.element.changed + '(' + path + ')');
                                    $(document).trigger(pages.const.event.element.changed, [path]);
                                }, this));
                            }, this));
                    }
                }
            },

            doDropCopy: function (target, object) {
                if (target && target.container.reference.path && object && object.type === 'element') {
                    // TODO: UI concept (copy/move handling) and implementation
                }
            },

            doDropMove: function (target, object) {
                if (target && target.container.reference.path && object && object.type === 'element') {
                    core.ajaxPost(pages.const.url.edit.move + object.reference.path, {
                        targetPath: target.container.reference.path,
                        targetType: target.container.reference.type,
                        before: target.before && target.before.reference.path ? target.before.reference.path : ''
                    }, {}, function (result) {
                        var oldParentPath = core.getParentPath(object.reference.path);
                        if (oldParentPath !== target.container.reference.path) {
                            $(document).trigger(pages.const.event.element.changed, [oldParentPath]);
                        }
                        $(document).trigger(pages.const.event.element.changed, [target.container.reference.path]);
                    }, function (xhr) {
                        core.alert('error', 'Error', 'Error on moving component', xhr);
                    });
                }
            }
        };

    })(window.composum.pages.actions, window.composum.pages, window.core);
})(window);
