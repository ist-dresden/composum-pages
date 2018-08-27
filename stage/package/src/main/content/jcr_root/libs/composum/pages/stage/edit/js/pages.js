(function (window) {
    window.composum = window.composum || {};
    window.composum.pages = window.composum.pages || {};

    (function (pages, core) {
        'use strict';

        pages.const = _.extend(pages.const || {}, {
            modes: {
                none: 'NONE',
                preview: 'PREVIEW', browse: 'BROWSE',
                edit: 'EDIT', develop: 'DEVELOP'
            },
            data: {
                body: {
                    mode: 'pages-mode',
                    locale: 'pages-locale'
                }
            },
            frameWrapperClass: 'composum-pages-stage-edit-frame_wrapper',
            frameClass: 'composum-pages-stage-edit-frame',
            editDialogsClass: 'composum-pages-stage-edit-dialogs',
            versionViewCssClass: 'composum-pages-version-view',
            css: {
                base_: 'composum-pages-',
                scope: {
                    _: 'scope-',
                    content: 'content',
                    site: 'site'
                }
            },
            event: {
                messagePattern: new RegExp('^([^{\\[]+)([{\\[].*[}\\]])$'),
                trigger: 'event:trigger',
                ready: 'pages:ready',
                scope: {
                    changed: 'scope:changed'
                },
                dialog: {
                    edit: 'dialog:edit',
                    alert: 'dialog:alert'
                },
                site: {
                    select: 'site:select',          // do it!...
                    selected: 'site:selected',      // done.
                    created: 'site:created',        // done.
                    changed: 'site:changed',        // done.
                    deleted: 'site:deleted'         // done.
                },
                page: {
                    view: 'page:view',              // do it!...
                    select: 'page:select',          // do it!...
                    selected: 'page:selected',      // done.
                    inserted: 'page:inserted',      // done.
                    changed: 'page:changed',        // done.
                    deleted: 'page:deleted',        // done.
                    containerRefs: 'page:containerRefs'
                },
                content: {
                    select: 'content:select',       // do it!...
                    selected: 'content:selected',   // done.
                    inserted: 'content:inserted',   // done.
                    changed: 'content:changed',     // done.
                    deleted: 'content:deleted',     // done.
                    moved: 'content:moved'          // done.
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
                path: {
                    select: 'path:select',          // do it!...
                    selected: 'path:selected'       // done.
                },
                dnd: {
                    finished: 'dnd:finished'        // done, reset state.
                }
            },
            url: {
                get: {
                    edit: '/bin/cpm/pages/edit',
                    _resourceInfo: '.resourceInfo.json',
                    pageData: '/bin/cpm/pages/edit.pageData.json'
                },
                edit: {
                    insert: '/bin/cpm/pages/edit.insertComponent.json',
                    move: '/bin/cpm/pages/edit.moveComponent.json'
                }
            },
            sling: {
                resourceType: 'sling:resourceType'
            },
            login: {
                id: 'composum-platform-commons-login-dialog',
                url: '/libs/composum/platform/security/login/dialog.html'
            },
            profile: {
                pages: {
                    aspect: 'pages',
                    scope: 'scope'      // the scope of the edit UI: 'site' or 'content'
                },
                page: {
                    tree: {
                        aspect: 'page-tree',
                        view: 'view',   // the current panel: 'tree' or 'search'
                        filter: 'filter',
                        path: 'path'
                    },
                    search: {
                        aspect: 'page-search',
                        scope: 'scope',
                        term: 'term'
                    }
                },
                develop: {
                    tree: {
                        aspect: 'develop-tree',
                        path: 'path'
                    }
                }
            },
            clipboard: {
                store: {
                    content: 'content',
                    element: 'element'
                }
            }
        });

        pages.log = log.getLogger("pages");

        pages.profile = {

            aspectBase: 'composum.pages.',
            aspects: {},

            get: function (aspect, key, defaultValue) {
                var object = pages.profile.aspects[aspect];
                if (!object) {
                    var item = localStorage.getItem(pages.profile.aspectBase + aspect);
                    if (item) {
                        object = JSON.parse(item);
                        pages.profile.aspects[aspect] = object;
                    }
                }
                var value = undefined;
                if (object) {
                    value = key ? object[key] : object;
                }
                return value !== undefined ? value : defaultValue;
            },

            set: function (aspect, key, value) {
                var object = pages.profile.get(aspect, undefined, {});
                if (key) {
                    object[key] = value;
                } else {
                    object = value;
                }
                pages.profile.aspects[aspect] = object;
                pages.profile.save(aspect);
            },

            save: function (aspect) {
                var value = pages.profile.aspects[aspect];
                if (value) {
                    localStorage.setItem(pages.profile.aspectBase + aspect, JSON.stringify(value));
                }
            }
        };

        pages.Reference = function (nameOrView, path, type, prim) {
            if (nameOrView instanceof Backbone.View) {
                nameOrView = nameOrView.$el;
            }
            if (nameOrView instanceof jQuery) {
                var reference = nameOrView.data('pages-edit-reference');
                if (reference) {
                    _.extend(this, reference);
                } else {
                    this.name = nameOrView.data('pages-edit-name');
                    this.path = path || nameOrView.data('pages-edit-path');
                    this.type = type || nameOrView.data('pages-edit-type');
                    this.prim = prim || nameOrView.data('pages-edit-prim');
                }
            } else {
                this.name = nameOrView; // resource name
                this.path = path;       // resource path
                this.type = type;       // resource type
                this.prim = prim;       // primary / component type
            }
        };
        _.extend(pages.Reference.prototype, {

            /**
             * check reference data on completeness
             */
            isComplete: function () {
                return this.name !== undefined && this.path !== undefined &&
                    this.type !== undefined && this.prim !== undefined;
            },

            /**
             * load reference information if necessary and call callback after data load if a callback is present
             */
            complete: function (callback) {
                if (!this.isComplete()) {
                    var u = pages.const.url.get;
                    var options = {};
                    if (this.type) {
                        options.data = {
                            type: this.type
                        };
                    }
                    core.ajaxGet(u.edit + u._resourceInfo + this.path, options, _.bind(function (data) {
                        // '' as fallback to prevent from infinite recursion..
                        if (!this.name) {
                            this.name = data.name ? data.name : '';
                        }
                        if (!this.type) {
                            this.type = data.type ? data.type : '';
                        }
                        if (!this.prim) {
                            this.prim = data.prim ? data.prim : '';
                        }
                        if (_.isFunction(callback)) {
                            callback(this);
                        }
                    }, this));
                } else {
                    if (_.isFunction(callback)) {
                        callback(this);
                    }
                }
            }
        });

        pages.$body = $('body');
        pages.current = {
            mode: pages.$body.data(pages.const.data.body.mode),
            locale: pages.$body.data(pages.const.data.body.locale),
            folder: undefined,
            site: undefined,
            page: undefined,
            element: undefined,
            dnd: {}
        };

        switch (pages.current.mode) {
            case 'PREVIEW':
            case 'BROWSE':
                pages.profile.set('mode', 'preview', pages.current.mode.toLowerCase());
                break;
            case 'EDIT':
            case 'DEVELOP':
                pages.profile.set('mode', 'edit', pages.current.mode.toLowerCase());
                break;
        }

        pages.getScope = function () {
            var c = pages.const.css;
            var g = pages.const.profile.pages;
            return pages.profile.get(g.aspect, g.scope, c.scope.content);
        };

        pages.setScope = function (scope, triggerEvent) {
            var c = pages.const.css;
            var g = pages.const.profile.pages;
            if (!scope) {
                scope = pages.profile.get(g.aspect, g.scope, c.scope.content);
            }
            if (scope !== pages.profile.get(g.aspect, g.scope)) {
                if (triggerEvent !== false) {
                    triggerEvent = true;
                }
                pages.profile.set(g.aspect, g.scope, scope);
                pages.$body.removeClass(c.base + c.scope._ + c.scope.content);
                pages.$body.removeClass(c.base + c.scope._ + c.scope.site);
                pages.$body.addClass(c.base + c.scope._ + scope);
            }
            if (triggerEvent) {
                var e = pages.const.event.scope;
                pages.log.debug('pages.trigger.' + e.changed + '(' + scope + ')');
                $(document).trigger(e.changed, [scope]);
            }
        };

        pages.isEditMode = function () {
            return pages.current.mode === 'EDIT' || pages.current.mode === 'DEVELOP';
        };

        pages.getPageData = function (path, callback) {
            core.ajaxGet(pages.const.url.get.pageData + path, {}, callback);
        };

        pages.versionsVisible = function () {
            return $('body').hasClass(pages.const.versionViewCssClass);
        };

        pages.contextTools = {

            log: log.getLogger("context"),

            initializers: [],

            getInitializers: function () {
                return pages.contextTools.initializers;
            },

            addTool: function (initializer) {
                if (!_.contains(pages.contextTools.initializers, initializer)) {
                    pages.contextTools.initializers.push(initializer);
                }
            }
        };

        //
        // Dialog container
        //

        pages.DialogHandler = Backbone.View.extend({

            openEditDialog: function (url, viewType, name, path, type, context, setupDialog, onNotFound) {
                core.ajaxGet(url + (path ? path : ''), {
                        data: {
                            name: name ? name : '',
                            type: type ? type : ''
                        }
                    },
                    _.bind(function (data) {
                        this.$el.append(data);
                        var $dialog = this.$el.children(':last-child');
                        var dialog = core.getWidget(this.el, $dialog[0], viewType);
                        if (dialog) {
                            dialog.data = {
                                name: name,
                                path: path,
                                type: type
                            };
                            if (_.isFunction(dialog.afterLoad)) {
                                dialog.afterLoad(name, path, type, context);
                            }
                            if (_.isFunction(setupDialog)) {
                                setupDialog(dialog);
                            }
                            if (dialog.useDefault) {
                                dialog.doSubmit(dialog.useDefault);
                            } else {
                                dialog.show();
                            }
                        }
                    }, this), _.bind(function (xhr) {
                        if (xhr.status === 404) {
                            if (_.isFunction(onNotFound)) {
                                onNotFound(name, path, type);
                            }
                        }
                    }, this));
            },

            openDialog: function (id, url, viewType, initView, callback) {
                this.getDialog(id, url, {}, viewType, _.bind(function (dialog) {
                    dialog.show(initView, callback);
                }, this));
            },

            getDialog: function (id, url, config, viewType, callback) {
                var dialog = core.getWidget(this.el, '#' + id, viewType);
                if (!dialog) {
                    core.ajaxGet(url, _.extend({dataType: 'html'}, config),
                        _.bind(function (data) {
                            this.$el.append(data);
                            dialog = core.getWidget(this.el, '#' + id, viewType);
                            if (dialog) {
                                if (_.isFunction(callback)) {
                                    callback(dialog);
                                }
                            }
                        }, this));
                } else {
                    if (_.isFunction(callback)) {
                        callback(dialog);
                    }
                }
                return dialog;
            }
        });

        pages.dialogHandler = core.getView('.' + pages.const.editDialogsClass, pages.DialogHandler);

        //
        // page frame message handler
        //

        window.addEventListener("message", function (event) {
            var message = pages.const.event.messagePattern.exec(event.data);
            if (message) {
                var args = JSON.parse(message[2]);
                switch (message[1]) {
                    case pages.const.event.element.selected:
                        // transform selection messages into the corresponding event for the edit frame components
                        if (args.path) {
                            pages.log.debug('pages.trigger.' + pages.const.event.path.selected + '(' + args.path + ')');
                            $(document).trigger(pages.const.event.path.selected, [args.path]);
                            var eventData = [
                                args.name,
                                args.path,
                                args.type
                            ];
                            pages.log.debug('pages.trigger.' + pages.const.event.element.selected + '(' + args.path + ')');
                            $(document).trigger(pages.const.event.element.selected, eventData);
                        } else {
                            pages.log.debug('pages.trigger.' + pages.const.event.path.selected + '()');
                            $(document).trigger(pages.const.event.path.selected, []);
                            pages.log.debug('pages.trigger.' + pages.const.event.element.selected + '()');
                            $(document).trigger(pages.const.event.element.selected, []);
                        }
                        break;
                    case pages.const.event.page.containerRefs:
                        // forward container references list to the edit frame components
                        pages.log.trace('pages.event.' + pages.const.event.page.containerRefs + '(' + message[2] + ')');
                        $(document).trigger(pages.const.event.page.containerRefs, [args]);
                        break;
                    case pages.const.event.element.insert:
                        // apply insert action messages from the edited page
                        pages.log.info('pages.event.element.insert(' + message[2] + ')');
                        if (args.type && args.target) {
                            core.ajaxPost(pages.const.url.edit.insert + args.target.path, {
                                elementType: args.type,
                                targetType: args.target.type,
                                before: args.before ? args.before : ''
                            }, {}, function (result) {
                                pages.editFrame.reloadPage();
                            }, function (xhr) {
                                core.alert('error', 'Error', 'Error on inserting component', xhr);
                            });
                        }
                        break;
                    case pages.const.event.element.move:
                        // apply move action messages from the edited page
                        pages.log.info('pages.event.element.move(' + message[2] + ')');
                        if (args.source && args.target) {
                            core.ajaxPost(pages.const.url.edit.move + args.source, {
                                targetPath: args.target.path,
                                targetType: args.target.type,
                                before: args.before ? args.before : ''
                            }, {}, function (result) {
                                pages.editFrame.reloadPage();
                            }, function (xhr) {
                                core.alert('error', 'Error', 'Error on moving component', xhr);
                            });
                        }
                        break;
                    case pages.const.event.dialog.edit:
                        // opens an edit dialog to perform editing of the content of the path transmitted
                        pages.log.trace('pages.event.dialog.edit(' + message[2] + ')');
                        if (args.target) {
                            var url = undefined;
                            if (args.dialog) {
                                url = args.dialog.url;
                            }
                            pages.dialogs.openEditDialog(args.target.name, args.target.path, args.target.type,
                                undefined/*context*/, url,
                                function (dialog) {
                                    if (args.values) {
                                        dialog.applyData(args.values);
                                    }
                                });
                        }
                        break;
                    case pages.const.event.trigger:
                        // triggers an event in the frame document context
                        pages.log.debug('pages.event.' + args.event + '(' + message[2] + ')');
                        $(document).trigger(args.event, args.data);
                        break;
                    case pages.const.event.dialog.alert:
                        // displays an alert message by opening an alert dialog
                        pages.log.trace('pages.event.dialog.alert(' + message[2] + ')');
                        core.alert(args.type, args.title, args.message, args.data);
                        break;
                }
            }
        }, false);

        //
        // DnD operations
        //

        pages.dnd = {

            insertNewElement: function (target, object, context) {
                if (!context) {
                    context = {
                        parent: target.container.reference,
                        before: target.before.reference
                    };
                }
                if (!context.parent.isComplete()) {
                    // get resource type and/or primary type of potentially synthetic parent and retry...
                    context.parent.complete(function () {
                        pages.dnd.insertNewElement(target, object, context);
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
            },

            moveElement: function (target, object) {
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
        };

        //
        // clipboard operations
        //

        /**
         * stored the path in the profile for a later 'paste' which will copy the content of the stored path
         */
        pages.clipboardCopyContent = function (path) {
            if (!path) {
                path = this.getCurrentPath();
            }
            pages.profile.set('pages', 'contentClipboard', {
                path: path
            });
        };

        /**
         * copy path from clipboard to the target path, open copy dialog if an error is occurring
         * @param path the target path for the copy operation
         */
        pages.clipboardPasteContent = function (path) {
            var clipboard = pages.profile.get('pages', 'contentClipboard');
            if (path && clipboard && clipboard.path) {
                var name = core.getNameFromPath(clipboard.path);
                // copy to the target with the same name
                core.ajaxPost("/bin/cpm/pages/edit.copyContent.json" + clipboard.path, {
                    targetPath: path,
                    name: name
                }, {}, _.bind(function (result) {
                    // trigger content change
                    $(document).trigger(pages.const.event.content.inserted, [path, name]);
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
        };

        //
        // login dialog and session expired (unauthorized) fallback
        //

        pages.LoginDialog = core.components.Dialog.extend({

            initialize: function (options) {
                core.components.Dialog.prototype.initialize.apply(this, [options]);
                this.form = core.getWidget(this.el, "form", core.components.FormWidget);
                this.form.$el.on('submit', _.bind(this.onSubmit, this));
                this.callsToRetry = [];
                this.showing = false;
            },

            resetOnShown: function () {
                core.components.Dialog.prototype.resetOnShown.apply(this);
                this.alert('warning', this.$el.data('message'));
            },

            onSubmit: function (event) {
                if (event) {
                    event.preventDefault();
                }
                this.submitForm();
                return false;
            },

            /**
             * collect all failed request calls to retry after successful login
             * @param retryThisFailedCall the call to retry after login
             */
            handleUnauthorized: function (retryThisFailedCall) {
                if (this.showing) {
                    // collect all failed calls during login
                    this.callsToRetry.push(retryThisFailedCall);
                } else {
                    // show login dialog and collect failed calls...
                    this.showing = true;
                    this.callsToRetry = [retryThisFailedCall];
                    this.show(undefined, _.bind(function () {
                        // retry after login all collected calls
                        this.callsToRetry.forEach(function (retryThisFailedCall) {
                            retryThisFailedCall();
                        });
                        this.showing = false;
                        this.callsToRetry = [];
                    }, this));
                }
            }
        });

        /**
         * the Pages Stage handler to resolve unauthorized request calls
         * @param retryThisFailedCall the call to retry after login
         * @see core.unauthorizedDelegate
         * @see core.ajaxCall
         */
        pages.handleUnauthorized = function (retryThisFailedCall) {
            var c = pages.const.login;
            pages.dialogHandler.getDialog(c.id, c.url, {}, pages.LoginDialog,
                _.bind(function (dialog) {
                    dialog.handleUnauthorized(retryThisFailedCall);
                }, this));
        };

        /**
         * check accessibility of the url as condition to execute a function
         * @param functionToCall the function to execute if url is accessible
         * @param urlToCheck the url to use as indicator
         */
        pages.retryIfUnauthorized = function (functionToCall, urlToCheck) {
            core.ajaxHead(urlToCheck, {}, _.bind(function () {
                functionToCall();
            }, this), _.bind(function () {
                pages.handleUnauthorized(functionToCall);
            }, this));
        };

    })(window.composum.pages, window.core);
})(window);
