/**
 * the 'pages' namespace and core Pages edit frame functions
 * strong dependency to: 'commons.js' (libs: 'backbone.js', 'underscore.js', 'loglevel.js', 'jquery.js')
 */
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
            trigger: {
                event: 'trigger:event',
                action: 'trigger:action',
                dialog: {
                    edit: 'dialog:edit',
                    generic: 'dialog:generic',
                    alert: 'dialog:alert'
                }
            },
            event: {
                messagePattern: new RegExp('^([^{\\[]+)([{\\[].*[}\\]])$'),
                pages: {
                    ready: 'pages:ready',
                    locale: 'pages:locale'
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
                    view: 'page:view',              // do it!...
                    select: 'page:select',          // do it!...
                    selected: 'page:selected',      // done.
                    inserted: 'page:inserted',      // done.
                    changed: 'page:changed',        // done.
                    deleted: 'page:deleted',        // done.
                    state: 'page:state',            // changed state of the page itself only (no structure change)
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
                asset: {
                    select: 'asset:select',         // do it!...
                    selected: 'asset:selected'      // done.
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
                get: {
                    edit: '/bin/cpm/pages/edit',
                    _resourceInfo: '.resourceInfo.json',
                    pageData: '/bin/cpm/pages/edit.pageData.json'
                },
                edit: {
                    insert: '/bin/cpm/pages/edit.insertElement.json',
                    move: '/bin/cpm/pages/edit.moveElement.json'
                }
            },
            sling: {
                resourceType: 'sling:resourceType'
            },
            login: {
                id: 'composum-platform-commons-login-dialog',
                url: '/libs/composum/platform/public/login/dialog.html'
            },
            profile: {
                pages: {
                    aspect: 'pages',
                    scope: 'scope',     // the scope of the edit UI: 'site' or 'content'
                    locale: 'locale'
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
                asset: {
                    tree: {
                        aspect: 'asset-tree',
                        view: 'view',
                        filter: 'filter',
                        path: 'path'
                    },
                    search: {
                        aspect: 'asset-search',
                        scope: 'scope',
                        term: 'term'
                    }
                },
                develop: {
                    tree: {
                        aspect: 'develop-tree',
                        path: 'path'
                    }
                },
                components: {
                    list: {
                        aspect: 'components-list',
                        filter: 'filter',
                        search: 'search'
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

        pages.$body = $('body');
        pages.current = function () {
            var g = pages.const.profile.pages;
            return {
                mode: pages.$body.data(pages.const.data.body.mode),
                locale: pages.profile.get(g.aspect, g.locale, 'en'),
                folder: undefined,
                site: undefined,
                page: undefined,
                element: undefined,
                dnd: {}
            }
        }();

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

        pages.getLocale = function () {
            return pages.current.locale;
        };

        pages.setLocale = function (locale, localeDefault) {
            if (pages.current.locale !== locale || (localeDefault && localeDefault !== pages.current.localeDefault)) {
                pages.current.locale = locale;
                pages.current.localeDefault = localeDefault;
                var e = pages.const.event.pages;
                var g = pages.const.profile.pages;
                pages.profile.set(g.aspect, g.locale, locale);
                pages.log.debug('pages.trigger.' + e.locale + '(' + locale + ')');
                pages.trigger('pages.locale.set', e.locale, [locale]);
            }
        };

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
                pages.trigger('pages.scope.set', e.changed, [scope]);
            }
        };

        pages.getPageUrl = function (/*optional*/ path, /*optional*/ locale) {
            if (!locale) {
                locale = pages.getLocale();
            }
            var url = core.getContextUrl((path || pages.current.page) + '.html');
            if (locale !== pages.current.localeDefault) {
                url += '?pages.locale=' + locale;
            }
            return url;
        };

        pages.isEditMode = function () {
            return pages.current.mode === 'EDIT' || pages.current.mode === 'DEVELOP';
        };

        pages.versionsVisible = function () {
            return $('body').hasClass(pages.const.versionViewCssClass);
        };

        /**
         * the general $(document) trigger point for the 'pages' widgets which is logging the event and the args
         * @param key the logging key which identifies the code source
         * @param event the event to trigger
         * @param args the event arguments (array of objects or values)
         * @param argsToLog optional; used for logging instead of the event args if args should not be logged
         */
        pages.trigger = function (key, event, /*array,optional*/ args, /*optional*/ argsToLog) {
            if (pages.log.getLevel() <= log.levels.WARN) { // use WARN to cause a call stack
                pages.log.warn('trigger@' + key + ' > ' + event
                    + JSON.stringify(argsToLog !== undefined ? argsToLog : (args ? args : [])));
            }
            try {
                $(document).trigger(event, args);
            } catch (ex) {
                pages.log.error('trigger@' + key + ' > ' + event
                    + JSON.stringify(argsToLog !== undefined ? argsToLog : (args ? args : [])) + '\n', ex);
            }
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

        pages.loadFrameContent = function (uri, callback) {
            var target = pages.current.page || pages.current.site;
            if (target) {
                uri += target;
            }
            uri += '?pages.view=' + pages.current.mode;
            core.getHtml(uri, _.bind(function (content) {
                callback(content);
            }, this));
        };

        //
        // Dialog container
        //

        pages.DialogHandler = Backbone.View.extend({

            openEditDialog: function (url, viewType, name, path, type, context, setupDialog, onNotFound) {
                core.ajaxGet(url + (path ? path : ''), {
                        data: {
                            name: name ? name : '',
                            type: type ? type : '',
                            'pages.locale': pages.getLocale()
                        }
                    },
                    _.bind(function (data) {
                        this.showDialogContent(data, viewType, name, path, type, context, setupDialog);
                    }, this), _.bind(function (xhr) {
                        if (xhr.status === 404) {
                            if (_.isFunction(onNotFound)) {
                                onNotFound(name, path, type);
                            }
                        }
                    }, this));
            },

            showDialogContent: function (content, viewType, name, path, type, context, setupDialog) {
                this.$el.append(content);
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
                    dialog.show();
                }
            },

            openDialog: function (id, url, viewType, initView, callback) {
                this.getDialog(id, url, {
                    data: {
                        'pages.locale': pages.getLocale()
                    }
                }, viewType, _.bind(function (dialog) {
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
                        // retry all collected calls after login
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
