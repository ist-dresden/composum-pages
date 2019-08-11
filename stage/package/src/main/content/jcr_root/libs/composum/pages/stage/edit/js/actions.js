/**
 * the set of actions usable in the 'action' attribute of a toolbar action tag or somewhere else
 *
 * general parameters:
 * @param event the event object in the UI
 * @param name the name of the content element
 * @param path the path of the content element
 * @param type the resource type of the content element
 */
(function (window) {
    window.composum = window.composum || {};
    window.composum.pages = window.composum.pages || {};
    window.composum.pages.actions = window.composum.pages.actions || {};

    (function (actions, pages, core) {
        'use strict';

        actions.const = _.extend(actions.const || {}, {
            version: {
                uri: {
                    pages: {
                        base: '/bin/cpm/pages/version',
                        _: {
                            checkpoint: '.checkpoint.json',
                            checkin: '.checkin.json',
                            checkout: '.checkout.json',
                            toggleCheckout: '.toggleCheckout.json',
                            lock: '.lock.json',
                            unlock: '.unlock.json',
                            toggleLock: '.toggleLock.json'
                        }
                    }
                }
            }
        });

        actions.trigger = function (event, action, reference) {
            if (_.isString(action)) {
                action = eval(action);
            }
            if (_.isFunction(action)) {
                action(event, reference.name, reference.path, reference.type);
            }
        };

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
                if (!path && pages.current.element) {
                    path = pages.current.element.reference
                        ? pages.current.element.reference.path : pages.current.element;
                }
                pages.profile.set('pages', 'elementClipboard', {
                    path: path
                });
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

            paste: function (event, name, path, type) {
                var clipboard = pages.profile.get('pages', 'elementClipboard');
                if (path && clipboard && clipboard.path) {
                    var sourceName = core.getNameFromPath(clipboard.path);
                    // copy to the target with the same name
                    core.ajaxPost("/bin/cpm/pages/edit.copyElement.json" + clipboard.path, {
                        targetPath: path,
                        targetType: type,
                        name: sourceName
                    }, {}, _.bind(function (result) {
                        // trigger content change
                        pages.trigger('actions.container.paste', pages.const.event.element.inserted, [
                            new pages.Reference(name, path, type),
                            new pages.Reference(result.reference)]);
                    }, this), function (xhr) {
                        actions.error('Error on copying element', xhr);
                    });
                }
            },

            delete: function (event, name, path, type) {
                actions.element.delete(event, name, path, type);
            }
        };

        actions.content = {

            copy: function (path) {
                pages.profile.set('pages', 'contentClipboard', {
                    path: path
                });
            },

            paste: function (path) {
                var clipboard = pages.profile.get('pages', 'contentClipboard');
                if (path && clipboard && clipboard.path) {
                    var name = core.getNameFromPath(clipboard.path);
                    // copy to the target with the same name
                    core.ajaxPost("/bin/cpm/pages/edit.copyContent.json" + clipboard.path, {
                        targetPath: path,
                        name: name
                    }, {}, _.bind(function (result) {
                        // trigger content change
                        pages.trigger('actions.content.paste', pages.const.event.content.inserted, [
                            new pages.Reference(name, path),
                            new pages.Reference(result.name, result.path, result.type, result.prim)]);
                    }, this), _.bind(function (result) {
                        // on error - display copy dialog initialized with the known data
                        var data = result.responseJSON;
                        pages.dialogs.openCopyContentDialog(undefined, clipboard.path, undefined,
                            _.bind(function (dialog) {
                                dialog.setValues(clipboard.path, path);
                                if (data.messages) {
                                    dialog.validationHint(data.messages[0].level, null, data.messages[0].text, data.messages[0].hint);
                                } else if (data.response) {
                                    dialog.validationHint(data.response.level, null, data.response.text);
                                }
                                dialog.hintsMessage('error');
                            }, this));
                    }, this));
                }
            }
        };

        actions.page = {

            open: function (event, name, path, type) {
                var e = pages.const.event.page;
                pages.trigger('actions.page.open', e.select, [path]);
            },

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
                actions.content.copy(path);
            },

            paste: function (event, name, path, type) {
                actions.content.paste(path);
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

            activate: function (event, name, path, type) {
                pages.dialogs.openActivatePageDialog(name, path, type);
            },

            revert: function (event, name, path, type) {
                pages.dialogs.openRevertPageDialog(name, path, type);
            },

            deactivate: function (event, name, path, type) {
                pages.dialogs.openDeactivatePageDialog(name, path, type);
            },

            checkpoint: function (event, name, path, type) {
                var u = actions.const.version.uri;
                core.ajaxPost(u.pages.base + u.pages._.checkpoint + path, {}, {},
                    _.bind(function (result) {
                        pages.trigger('actions.page.checkpoint', pages.const.event.page.state,
                            [new pages.Reference(name, path, type)]);
                    }, this), function (xhr) {
                        actions.error('Error on checkpoint', xhr);
                    });
            },

            checkout: function (event, name, path, type) {
                var u = actions.const.version.uri;
                core.ajaxPost(u.pages.base + u.pages._.checkout + path, {}, {},
                    _.bind(function (result) {
                        pages.trigger('actions.page.checkout', pages.const.event.page.state,
                            [new pages.Reference(name, path, type)]);
                    }, this), function (xhr) {
                        actions.error('Error on checkout', xhr);
                    });
            },

            checkin: function (event, name, path, type) {
                var u = actions.const.version.uri;
                core.ajaxPost(u.pages.base + u.pages._.checkin + path, {}, {},
                    _.bind(function (result) {
                        pages.trigger('actions.page.checkin', pages.const.event.page.state,
                            [new pages.Reference(name, path, type)]);
                    }, this), function (xhr) {
                        actions.error('Error on checkin', xhr);
                    });
            },

            toggleCheckout: function (event, name, path, type) {
                var u = actions.const.version.uri;
                core.ajaxPost(u.pages.base + u.pages._.toggleCheckout + path, {}, {},
                    _.bind(function (result) {
                        pages.trigger('actions.page.toggleCheckout', pages.const.event.page.state,
                            [new pages.Reference(name, path, type)]);
                    }, this), function (xhr) {
                        actions.error('Error on toggle checkout', xhr);
                    });
            },

            lock: function (event, name, path, type) {
                var u = actions.const.version.uri;
                core.ajaxPost(u.pages.base + u.pages._.lock + path, {}, {},
                    _.bind(function (result) {
                        pages.trigger('actions.page.lock', pages.const.event.page.state,
                            [new pages.Reference(name, path, type)]);
                    }, this), function (xhr) {
                        actions.error('Error on lock page', xhr);
                    });
            },

            unlock: function (event, name, path, type) {
                var u = actions.const.version.uri;
                core.ajaxPost(u.pages.base + u.pages._.unlock + path, {}, {},
                    _.bind(function (result) {
                        pages.trigger('actions.page.unlock', pages.const.event.page.state,
                            [new pages.Reference(name, path, type)]);
                    }, this), function (xhr) {
                        actions.error('Error on unlock page', xhr);
                    });
            },

            toggleLock: function (event, name, path, type) {
                var u = actions.const.version.uri;
                core.ajaxPost(u.pages.base + u.pages._.toggleLock + path, {}, {},
                    _.bind(function (result) {
                        pages.trigger('actions.page.toggleLock', pages.const.event.page.state,
                            [new pages.Reference(name, path, type)]);
                    }, this), function (xhr) {
                        actions.error('Error on toggle lock', xhr);
                    });
            }
        };

        actions.site = {

            open: function (event, name, path, type) {
                var e = pages.const.event.site;
                pages.trigger('actions.site.open', e.select, [path]);
            },

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
                actions.content.copy(path);
            },

            paste: function (event, name, path, type) {
                actions.content.paste(path);
            },

            delete: function (event, name, path, type) {
                pages.dialogs.openDeleteSiteDialog(name, path, type);
            },

            manage: function (event) {
                pages.dialogs.openManageSitesDialog();
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
                actions.content.copy(path);
            },

            paste: function (event, name, path, type) {
                actions.content.paste(path);
            },

            delete: function (event, name, path, type) {
                pages.dialogs.openDeleteContentDialog('folder', name, path, type);
            }
        };

        actions.file = {

            upload: function (event, name, path, type) {
                pages.dialogs.openUploadFileDialog(name, path, type);
            },

            download: function (event, name, path, type) {
                window.open(core.getContextUrl(path), '_blank');
            },

            rename: function (event, name, path, type) {
                pages.dialogs.openRenameContentDialog(name, path, type)
            },

            move: function (event, name, path, type) {
                pages.dialogs.openMoveContentDialog(name, path, type)
            },

            copy: function (event, name, path, type) {
                actions.content.copy(path);
            },

            delete: function (event, name, path, type) {
                pages.dialogs.openDeleteContentDialog('file', name, path, type);
            }
        };

        actions.component = {

            folder: {

                edit: function (event, name, path, type) {
                    pages.dialogs.openEditDialog(name, path, type, undefined,
                        '/libs/composum/pages/stage/edit/default/develop/component/folder.html');
                },

                insertFile: function (event, name, path, type) {
                    pages.dialogs.openNewFileDialog(name, path, type, pages.dialogs.const.edit.url._add._source);
                }
            },

            create: function (event, name, path, type) {
                pages.dialogs.openEditDialog(name, path, type, undefined,
                    '/libs/composum/pages/stage/edit/default/develop/component/create.html');
            },

            fromTemplate: function (event, name, path, type) {
                pages.dialogs.openEditDialog(name, path, type, undefined,
                    '/libs/composum/pages/stage/edit/default/develop/component/create.template.html');
            },

            edit: function (event, name, path, type) {
                pages.dialogs.openEditDialog(name, path, type, undefined,
                    '/libs/composum/pages/stage/edit/default/develop/component/properties.html');
            },

            manageElements: function (event, name, path, type) {
                pages.dialogs.openEditDialog(name, path, type, undefined,
                    '/libs/composum/pages/stage/edit/default/develop/component/manage.html');
            },

            rename: function (event, name, path, type) {
                pages.dialogs.openRenameContentDialog(name, path, type)
            },

            move: function (event, name, path, type) {
                pages.dialogs.openMoveContentDialog(name, path, type)
            },

            copy: function (event, name, path, type) {
                actions.content.copy(path);
            },

            paste: function (event, name, path, type) {
                actions.content.paste(path);
            },

            delete: function (event, name, path, type) {
                pages.dialogs.openDeleteContentDialog('component', name, path, type);
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
                            before: target.before ? target.before.reference : undefined
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
                                }, {}, _.bind(function (result) {
                                    pages.trigger('actions.dnd.insert', pages.const.event.element.inserted, [
                                        new pages.Reference(name, path, type),
                                        new pages.Reference(result.reference)]);
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
                            pages.trigger('actions.dnd.move', pages.const.event.element.changed, [new pages.Reference(undefined, oldParentPath)]);
                        }
                        pages.trigger('actions.dnd.move', pages.const.event.element.changed, [target.container.reference]);
                    }, function (xhr) {
                        core.alert('error', 'Error', 'Error on moving component', xhr);
                    });
                }
            },

            doZoneDrop: function (zone, object) {
                if (zone && zone.path && object && object.reference.path) {
                    var data = {
                        '_charset_': 'UTF-8'
                    };
                    if (zone.type) {
                        data['sling:resourceType'] = zone.type;
                    }
                    if (zone.prim) {
                        data['jcr:primaryType'] = zone.prim;
                    }
                    data[zone.property] = object.reference.path;
                    core.ajaxPost(zone.path, data, {}, function (result) {
                        var event = zone.event ? zone.event : pages.const.event.element.changed;
                        var reference = new pages.Reference(zone);
                        pages.trigger('actions.dnd.zone', event, [reference]);
                    }, function (xhr) {
                        core.alert('error', 'Error', 'Error on moving component', xhr);
                    });
                }
            }
        };

        actions.error = function (title, xhr) {
            var msgs = xhr.responseJSON.messages;
            if (msgs && msgs.length > 0) {
                core.alert(msgs[0].level, msgs[0].text, msgs[0].hint);
            } else {
                core.alert('error', 'Error', title);
            }
        };

    })(window.composum.pages.actions, window.composum.pages, window.core);
})(window);
